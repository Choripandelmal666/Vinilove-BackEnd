package com.vinilove.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import com.vinilove.dto.Requests.LoginRequest;
import com.vinilove.model.Usuario;
import com.vinilove.repository.CarritoItemRepository;
import com.vinilove.repository.CarritoRepository;
import com.vinilove.repository.MensajeContactoRepository;
import com.vinilove.repository.OfertaRepository;
import com.vinilove.repository.PedidoRepository;
import com.vinilove.repository.PerfilVendedorRepository;
import com.vinilove.repository.ProductoRepository;
import com.vinilove.repository.PublicacionBlogRepository;
import com.vinilove.repository.UsuarioRepository;

class ViniloveServiceTest {

	private UsuarioRepository usuarios;
	private PerfilVendedorRepository perfiles;
	private ViniloveService servicio;
	private Usuario admin;

	@BeforeEach
	void preparar() {
		usuarios = org.mockito.Mockito.mock(UsuarioRepository.class);
		perfiles = org.mockito.Mockito.mock(PerfilVendedorRepository.class);
		servicio = new ViniloveService(
				usuarios,
				perfiles,
				org.mockito.Mockito.mock(ProductoRepository.class),
				org.mockito.Mockito.mock(OfertaRepository.class),
				org.mockito.Mockito.mock(CarritoRepository.class),
				org.mockito.Mockito.mock(CarritoItemRepository.class),
				org.mockito.Mockito.mock(PedidoRepository.class),
				org.mockito.Mockito.mock(MensajeContactoRepository.class),
				org.mockito.Mockito.mock(PublicacionBlogRepository.class));
		admin = new Usuario();
		admin.setId(1L);
		admin.setCorreo("admin@vinilove.com");
		admin.setNombre("Admin");
		admin.setRol("admin");
		admin.setPassword(new BCryptPasswordEncoder().encode("ChangeMe-Local-1234"));
		when(usuarios.findByCorreoIgnoreCase("admin@vinilove.com")).thenReturn(Optional.of(admin));
		when(perfiles.findByUsuario_Id(1L)).thenReturn(Optional.empty());
	}

	@Test
	void loginCorrectoDevuelveElCorreo() {
		LoginRequest datos = new LoginRequest();
		datos.correo = "admin@vinilove.com";
		datos.password = "ChangeMe-Local-1234";
		assertEquals("admin@vinilove.com", servicio.login(datos).correo);
	}

	@Test
	void loginConClaveMalaFalla() {
		LoginRequest datos = new LoginRequest();
		datos.correo = "admin@vinilove.com";
		datos.password = "clave-mala";
		ResponseStatusException error = assertThrows(ResponseStatusException.class, () -> servicio.login(datos));
		assertEquals(HttpStatus.UNAUTHORIZED, error.getStatusCode());
	}
}