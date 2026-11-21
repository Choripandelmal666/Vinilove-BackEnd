package com.vinilove.controller;

import java.net.URI;
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

import com.vinilove.dto.Requests.ProductoRequest;
import com.vinilove.dto.Responses.ProductoResponse;
import com.vinilove.service.ViniloveService;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

	private final ViniloveService viniloveService;

	public ProductoController(ViniloveService viniloveService) {
		this.viniloveService = viniloveService;
	}

	@GetMapping
	public List<ProductoResponse> findAll() {
		return viniloveService.listarProductos();
	}

	@GetMapping("/{id}")
	public ProductoResponse findById(@PathVariable String id) {
		return viniloveService.buscarProducto(id);
	}

	@PostMapping
	public ResponseEntity<ProductoResponse> create(@RequestBody ProductoRequest datos) {
		ProductoResponse creado = viniloveService.crearProducto(datos);
		return ResponseEntity.created(URI.create("/api/productos/" + creado.id)).body(creado);
	}

	@PutMapping("/{id}")
	public ProductoResponse update(@PathVariable String id, @RequestBody ProductoRequest datos) {
		return viniloveService.editarProducto(id, datos);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable String id) {
		viniloveService.eliminarProducto(id);
		return ResponseEntity.noContent().build();
	}
}
