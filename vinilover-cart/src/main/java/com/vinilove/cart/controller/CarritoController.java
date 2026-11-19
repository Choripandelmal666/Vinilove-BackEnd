package com.vinilove.cart.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.vinilove.cart.CartDtos.ItemRequest;
import com.vinilove.cart.CartDtos.ItemResponse;
import com.vinilove.cart.service.CartService;

@RestController
@RequestMapping("/api/carrito")
public class CarritoController {

	private final CartService carrito;

	public CarritoController(CartService carrito) {
		this.carrito = carrito;
	}

	@GetMapping("/{usuarioId}")
	public List<ItemResponse> ver(@PathVariable Long usuarioId, @AuthenticationPrincipal Jwt jwt) {
		validarPropietario(usuarioId, jwt);
		return carrito.ver(usuarioId);
	}

	@PutMapping("/{usuarioId}")
	public List<ItemResponse> reemplazar(@PathVariable Long usuarioId, @RequestBody List<ItemRequest> items,
			@AuthenticationPrincipal Jwt jwt) {
		validarPropietario(usuarioId, jwt);
		return carrito.reemplazar(usuarioId, items);
	}

	private void validarPropietario(Long usuarioId, Jwt jwt) {
		boolean administrador = "ADMIN".equals(jwt.getClaimAsString("role"));
		if (!administrador && !usuarioId.toString().equals(jwt.getSubject())) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes acceder al carrito de otro usuario.");
		}
	}
}
