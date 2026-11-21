package com.vinilove.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

import com.vinilove.dto.Requests.PedidoRequest;
import com.vinilove.dto.Responses.PedidoResponse;
import com.vinilove.service.ViniloveService;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

	private final ViniloveService viniloveService;

	public PedidoController(ViniloveService viniloveService) {
		this.viniloveService = viniloveService;
	}

	@GetMapping
	public List<PedidoResponse> listar() {
		return viniloveService.listarPedidos();
	}

	@GetMapping("/usuario/{usuarioId}")
	@PreAuthorize("#p0.toString() == authentication.name or hasRole('ADMIN')")
	public List<PedidoResponse> deUsuario(@PathVariable Long usuarioId) {
		return viniloveService.pedidosDe(usuarioId);
	}

	@PostMapping
	public PedidoResponse crear(@RequestBody PedidoRequest datos) {
		return viniloveService.crearPedido(datos);
	}
}
