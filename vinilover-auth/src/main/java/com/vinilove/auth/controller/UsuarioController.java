package com.vinilove.auth.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vinilove.auth.AuthDtos.GoogleRequest;
import com.vinilove.auth.AuthDtos.LoginRequest;
import com.vinilove.auth.AuthDtos.PerfilRequest;
import com.vinilove.auth.AuthDtos.RegistroRequest;
import com.vinilove.auth.AuthDtos.UsuarioResponse;
import com.vinilove.auth.AuthDtos.VendedorRequest;
import com.vinilove.auth.service.AuthService;

@RestController
@RequestMapping("/api")
public class UsuarioController {

	private final AuthService auth;

	public UsuarioController(AuthService auth) {
		this.auth = auth;
	}

	@PostMapping("/usuarios/registro")
	public UsuarioResponse registro(@RequestBody RegistroRequest datos) {
		return auth.registrar(datos);
	}

	@PostMapping("/usuarios/login")
	public UsuarioResponse login(@RequestBody LoginRequest datos) {
		return auth.login(datos);
	}

	@PostMapping("/usuarios/google")
	public UsuarioResponse google(@RequestBody GoogleRequest datos) {
		return auth.loginConGoogle(datos);
}

	@GetMapping("/usuarios")
	@PreAuthorize("hasRole('ADMIN')")
	public List<UsuarioResponse> listar() {
		return auth.listar();
	}

	@PutMapping("/usuarios/{id}")
	@PreAuthorize("#p0.toString() == authentication.name or hasRole('ADMIN')")
	public UsuarioResponse actualizar(@PathVariable Long id, @RequestBody PerfilRequest datos) {
		return auth.actualizarPerfil(id, datos);
	}

	@DeleteMapping("/usuarios/{id}")
	@PreAuthorize("#p0.toString() == authentication.name or hasRole('ADMIN')")
	public ResponseEntity<Void> eliminar(@PathVariable Long id) {
		auth.eliminar(id);
		return ResponseEntity.noContent().build();
	}

	@PutMapping("/vendedores/{usuarioId}")
	@PreAuthorize("#p0.toString() == authentication.name or hasRole('ADMIN')")
	public UsuarioResponse guardarVendedor(@PathVariable Long usuarioId, @RequestBody VendedorRequest datos) {
		return auth.guardarVendedor(usuarioId, datos);
	}
}
