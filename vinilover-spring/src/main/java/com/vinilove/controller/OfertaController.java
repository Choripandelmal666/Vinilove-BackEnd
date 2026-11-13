package com.vinilove.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vinilove.dto.Requests.OfertaRequest;
import com.vinilove.dto.Responses.OfertaResponse;
import com.vinilove.dto.Responses.ProductoResponse;
import com.vinilove.service.ViniloveService;

@RestController
@RequestMapping("/api/ofertas")
public class OfertaController {

	private final ViniloveService viniloveService;

	public OfertaController(ViniloveService viniloveService) {
		this.viniloveService = viniloveService;
	}

	@GetMapping
	public List<ProductoResponse> activas() {
		return viniloveService.listarOfertas();
	}

	@PostMapping
	public OfertaResponse create(@RequestBody OfertaRequest datos) {
		return viniloveService.crearOferta(datos);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		viniloveService.eliminarOferta(id);
		return ResponseEntity.noContent().build();
	}
}
