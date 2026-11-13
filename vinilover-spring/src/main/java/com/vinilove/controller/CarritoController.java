package com.vinilove.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vinilove.dto.Requests.ItemRequest;
import com.vinilove.dto.Responses.CarritoItemResponse;
import com.vinilove.service.ViniloveService;

@RestController
@RequestMapping("/api/carrito")
public class CarritoController {

	private final ViniloveService viniloveService;

	public CarritoController(ViniloveService viniloveService) {
		this.viniloveService = viniloveService;
	}

	@GetMapping("/{usuarioId}")
	public List<CarritoItemResponse> ver(@PathVariable Long usuarioId) {
		return viniloveService.verCarrito(usuarioId);
	}

	@PutMapping("/{usuarioId}")
	public List<CarritoItemResponse> reemplazar(@PathVariable Long usuarioId, @RequestBody List<ItemRequest> items) {
		return viniloveService.reemplazarCarrito(usuarioId, items);
	}
}
