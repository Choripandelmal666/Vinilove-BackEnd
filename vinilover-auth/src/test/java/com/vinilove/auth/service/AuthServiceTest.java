package com.vinilove.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import com.vinilove.auth.AuthDtos.LoginRequest;
import com.vinilove.auth.model.Usuario;
import com.vinilove.auth.repository.UsuarioRepository;
import com.vinilove.auth.security.JwtTokenService;

class AuthServiceTest {

	private final PasswordEncoder encoder = new BCryptPasswordEncoder();
	private UsuarioRepository usuarios;
	private JwtTokenService tokens;
	private AuthService servicio;
	private Usuario admin;

	@BeforeEach
	void preparar() {
		usuarios = org.mockito.Mockito.mock(UsuarioRepository.class);
		tokens = org.mockito.Mockito.mock(JwtTokenService.class);
		servicio = new AuthService(usuarios, encoder, tokens, "client-de-prueba");
		admin = new Usuario();
		admin.id = 1L;
		admin.correo = "admin@vinilove.com";
		admin.nombre = "Admin";
		admin.rol = "admin";
		admin.password = encoder.encode("ChangeMe-Local-1234");
		when(usuarios.findByCorreoIgnoreCase("admin@vinilove.com")).thenReturn(Optional.of(admin));
		when(tokens.emitir(admin)).thenReturn("token-de-prueba");
	}

	@Test
	void loginCorrectoDevuelveToken() {
		var respuesta = servicio.login(new LoginRequest("admin@vinilove.com", "ChangeMe-Local-1234"));
		assertEquals("token-de-prueba", respuesta.token());
		assertEquals("admin", respuesta.rol());
	}

	@Test
	void loginConClaveMalaFalla() {
		ResponseStatusException error = assertThrows(ResponseStatusException.class,
				() -> servicio.login(new LoginRequest("admin@vinilove.com", "clave-mala")));
		assertEquals(HttpStatus.UNAUTHORIZED, error.getStatusCode());
	}
}