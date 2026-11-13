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

import com.vinilove.dto.Requests.BlogRequest;
import com.vinilove.dto.Responses.BlogResponse;
import com.vinilove.service.ViniloveService;

@RestController
@RequestMapping("/api/blog")
public class BlogController {

	private final ViniloveService viniloveService;

	public BlogController(ViniloveService viniloveService) {
		this.viniloveService = viniloveService;
	}

	@GetMapping
	public List<BlogResponse> listar() {
		return viniloveService.listarBlog();
	}

	@PostMapping
	public BlogResponse crear(@RequestBody BlogRequest datos) {
		return viniloveService.crearBlog(datos);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable String id) {
		viniloveService.eliminarBlog(id);
		return ResponseEntity.noContent().build();
	}
}
