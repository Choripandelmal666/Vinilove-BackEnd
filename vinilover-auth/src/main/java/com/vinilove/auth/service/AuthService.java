package com.vinilove.auth.service;

import java.util.List;
import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.vinilove.auth.AuthDtos.LoginRequest;
import com.vinilove.auth.AuthDtos.PerfilRequest;
import com.vinilove.auth.AuthDtos.RegistroRequest;
import com.vinilove.auth.AuthDtos.UsuarioResponse;
import com.vinilove.auth.AuthDtos.VendedorRequest;
import com.vinilove.auth.model.Usuario;
import com.vinilove.auth.repository.UsuarioRepository;
import com.vinilove.auth.security.JwtTokenService;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import com.vinilove.auth.AuthDtos.GoogleRequest;

@Service
@Transactional
public class AuthService {

	private final UsuarioRepository usuarios;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenService tokens;
	private final String googleClientId;
	private final ObjectMapper json = new ObjectMapper();

	public AuthService(
			UsuarioRepository usuarios,
			PasswordEncoder passwordEncoder,
			JwtTokenService tokens,
			@Value("${vinilove.google.client-id:}") String googleClientId) {
		this.usuarios = usuarios;
		this.passwordEncoder = passwordEncoder;
		this.tokens = tokens;
		this.googleClientId = googleClientId;
	}

	public UsuarioResponse registrar(RegistroRequest datos) {
		if (datos == null || vacio(datos.correo()) || vacio(datos.nombre()) || vacio(datos.password())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Faltan datos del registro.");
		}
		if (datos.password().length() < 8) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña debe tener al menos 8 caracteres.");
		}
		if (usuarios.existsByCorreoIgnoreCase(datos.correo().trim())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Ese correo ya está registrado.");
		}
		if (datos.run() != null && usuarios.existsByRunIgnoreCase(datos.run().trim())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Ese RUN ya está registrado.");
		}
		if (datos.nombreUsuario() != null && usuarios.existsByNombreUsuarioIgnoreCase(datos.nombreUsuario().trim())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Ese nombre de usuario ya está registrado.");
		}
		String rol = datos.rol() == null || datos.rol().isBlank() ? "cliente" : datos.rol().toLowerCase(Locale.ROOT);
		if (!rol.equals("cliente") && !rol.equals("vendedor")) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El rol debe ser cliente o vendedor.");
		}
		Usuario usuario = new Usuario();
		usuario.run = datos.run();
		usuario.correo = datos.correo().trim();
		usuario.nombre = datos.nombre().trim();
		usuario.apellidos = datos.apellidos() == null ? "" : datos.apellidos().trim();
		usuario.password = passwordEncoder.encode(datos.password());
		usuario.rol = rol;
		usuario.nombreUsuario = datos.nombreUsuario();
		if (rol.equals("vendedor")) {
			usuario.nombreTienda = valor(datos.nombreTienda(), "Tienda de " + usuario.nombre);
			usuario.descripcionTienda = valor(datos.descripcion(), "Perfil de vendedor en Vinilove.");
			usuario.ciudad = valor(datos.ciudad(), "Santiago");
		}
		return respuesta(usuarios.save(usuario), null);
	}

	public UsuarioResponse login(LoginRequest datos) {
		if (datos == null || datos.password() == null) {
			throw credencialesInvalidas();
		}
		Usuario usuario = usuarios.findByCorreoIgnoreCase(datos.correo() == null ? "" : datos.correo().trim())
				.orElseThrow(this::credencialesInvalidas);
		boolean hash = usuario.password.startsWith("$2");
		boolean valida = hash ? passwordEncoder.matches(datos.password(), usuario.password)
				: usuario.password.equals(datos.password());
		if (!valida) {
			throw credencialesInvalidas();
		}
		if (!hash) {
			usuario.password = passwordEncoder.encode(datos.password());
		}
		return respuesta(usuario, tokens.emitir(usuario));
	}

	public UsuarioResponse loginConGoogle(GoogleRequest datos) {
	if (datos == null || vacio(datos.credential())) {
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Falta la credencial de Google.");
	}
	if (googleClientId == null || googleClientId.isBlank()) {
		throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Falta GOOGLE_CLIENT_ID en el servidor.");
	}
	Map<String, Object> google = consultarGoogle(datos.credential());
	String correo = texto(google.get("email")).trim();
	boolean audienciaCorrecta = googleClientId.equals(texto(google.get("aud")));
	boolean correoVerificado = "true".equalsIgnoreCase(texto(google.get("email_verified")));
	if (!audienciaCorrecta || !correoVerificado || correo.isBlank()) {
		throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Google no reconoció esta cuenta.");
	}
	Usuario usuario = usuarios.findByCorreoIgnoreCase(correo)
			.orElseGet(() -> crearDesdeGoogle(google, correo));
	return respuesta(usuario, tokens.emitir(usuario));
}

private Map<String, Object> consultarGoogle(String credential) {
	try {
		String url = "https://oauth2.googleapis.com/tokeninfo?id_token="
				+ URLEncoder.encode(credential, StandardCharsets.UTF_8);
		HttpRequest request = HttpRequest.newBuilder(URI.create(url)).GET().build();
		HttpResponse<String> response = HttpClient.newHttpClient()
				.send(request, HttpResponse.BodyHandlers.ofString());
		if (response.statusCode() != 200) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Google no reconoció esta cuenta.");
		}
		return json.readValue(response.body(), new TypeReference<Map<String, Object>>() {});
	} catch (ResponseStatusException e) {
		throw e;
	} catch (Exception e) {
		throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "No se pudo consultar a Google.");
	}
}

private Usuario crearDesdeGoogle(Map<String, Object> google, String correo) {
	String digitos = texto(google.get("sub")).replaceAll("\\D", "");
	if (digitos.length() < 8) {
		digitos = digitos + "00000000";
	}
	String run = "G" + digitos.substring(digitos.length() - 8);
	while (usuarios.existsByRunIgnoreCase(run)) {
		digitos = digitos + "1";
		run = "G" + digitos.substring(digitos.length() - 8);
	}
	String base = correo.split("@")[0].replaceAll("[^a-zA-Z0-9]", "");
	if (base.isBlank()) {
		base = "google";
	}
	if (base.length() > 30) {
		base = base.substring(0, 30);
	}
	String nombreUsuario = base;
	int n = 1;
	while (usuarios.existsByNombreUsuarioIgnoreCase(nombreUsuario)) {
		nombreUsuario = base + n;
		n++;
	}
	String nombre = texto(google.get("given_name"));
	if (nombre.isBlank()) {
		nombre = texto(google.get("name"));
	}
	if (nombre.isBlank()) {
		nombre = base;
	}
	Usuario usuario = new Usuario();
	usuario.run = run;
	usuario.correo = correo;
	usuario.nombre = nombre;
	usuario.apellidos = texto(google.get("family_name"));
	usuario.password = passwordEncoder.encode(UUID.randomUUID().toString());
	usuario.rol = "cliente";
	usuario.nombreUsuario = nombreUsuario;
	return usuarios.save(usuario);
}

private String texto(Object valor) {
	return valor == null ? "" : String.valueOf(valor);
}

	public List<UsuarioResponse> listar() {
		return usuarios.findAll().stream().map(usuario -> respuesta(usuario, null)).toList();
	}

	public UsuarioResponse actualizarPerfil(Long id, PerfilRequest datos) {
		Usuario usuario = obligatorio(id);
		if (datos.nombreUsuario() != null && !datos.nombreUsuario().isBlank()) {
			usuario.nombreUsuario = datos.nombreUsuario().trim();
		}
		if (datos.fotoPerfil() != null && !datos.fotoPerfil().isBlank()) {
			usuario.fotoPerfil = datos.fotoPerfil();
		}
		return respuesta(usuario, null);
	}

	public UsuarioResponse guardarVendedor(Long id, VendedorRequest datos) {
		Usuario usuario = obligatorio(id);
		usuario.rol = "vendedor";
		usuario.nombreTienda = valor(datos.nombreTienda(), "Tienda de " + usuario.nombre);
		usuario.descripcionTienda = valor(datos.descripcion(), "Perfil de vendedor en Vinilove.");
		usuario.ciudad = valor(datos.ciudad(), "Santiago");
		return respuesta(usuario, null);
	}

	public void eliminar(Long id) {
		Usuario usuario = obligatorio(id);
		if ("admin@vinilove.com".equalsIgnoreCase(usuario.correo) || "admin".equals(usuario.rol)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se puede eliminar una cuenta administradora.");
		}
		usuarios.delete(usuario);
	}

	private Usuario obligatorio(Long id) {
		return usuarios.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado."));
	}

	private UsuarioResponse respuesta(Usuario usuario, String token) {
		return new UsuarioResponse(usuario.id, token, usuario.run, usuario.correo, usuario.nombre, usuario.apellidos,
				usuario.rol, usuario.nombreUsuario, usuario.fotoPerfil, usuario.nombreTienda,
				usuario.descripcionTienda, usuario.ciudad);
	}

	private ResponseStatusException credencialesInvalidas() {
		return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Correo o contraseña incorrectos.");
	}

	private boolean vacio(String valor) {
		return valor == null || valor.isBlank();
	}

	private String valor(String actual, String porDefecto) {
		return actual == null || actual.isBlank() ? porDefecto : actual.trim();
	}
}
