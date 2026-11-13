package com.vinilove.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vinilove.dto.Requests.LoginRequest;
import com.vinilove.dto.Requests.PerfilRequest;
import com.vinilove.dto.Requests.RegistroRequest;
import com.vinilove.dto.Requests.VendedorRequest;
import com.vinilove.dto.Responses.UsuarioResponse;
import com.vinilove.service.ViniloveService;

@RestController
@RequestMapping("/api")
public class UsuarioController {

	private final ViniloveService viniloveService;

	public UsuarioController(ViniloveService viniloveService) {
		this.viniloveService = viniloveService;
	}

	@PostMapping("/usuarios/registro")
	public UsuarioResponse registro(@RequestBody RegistroRequest datos) {
		return viniloveService.registrar(datos);
	}

	@PostMapping("/usuarios/login")
	public UsuarioResponse login(@RequestBody LoginRequest datos) {
		return viniloveService.login(datos);
	}

	@GetMapping("/usuarios")
	public List<UsuarioResponse> listar() {
		return viniloveService.listarUsuarios();
	}

	@PutMapping("/usuarios/{id}")
	public UsuarioResponse actualizar(@PathVariable Long id, @RequestBody PerfilRequest datos) {
		return viniloveService.actualizarPerfil(id, datos);
	}

	@DeleteMapping("/usuarios/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable Long id) {
		viniloveService.eliminarUsuario(id);
		return ResponseEntity.noContent().build();
	}

	@PutMapping("/vendedores/{usuarioId}")
	public UsuarioResponse guardarVendedor(@PathVariable Long usuarioId, @RequestBody VendedorRequest datos) {
		return viniloveService.guardarVendedor(usuarioId, datos);
	}
}
