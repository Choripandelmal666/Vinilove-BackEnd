package com.vinilove.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vinilove.dto.Requests.ContactoRequest;
import com.vinilove.dto.Responses.MensajeResponse;
import com.vinilove.service.ViniloveService;

@RestController
@RequestMapping("/api/contacto")
public class ContactoController {

	private final ViniloveService viniloveService;

	public ContactoController(ViniloveService viniloveService) {
		this.viniloveService = viniloveService;
	}

	@GetMapping
	public List<MensajeResponse> listar() {
		return viniloveService.listarMensajes();
	}

	@PostMapping
	public MensajeResponse crear(@RequestBody ContactoRequest datos) {
		return viniloveService.crearMensaje(datos);
	}
}
