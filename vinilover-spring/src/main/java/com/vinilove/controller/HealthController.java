package com.vinilove.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
public class HealthController {

	@GetMapping
	public Map<String, Object> health() {
		return Map.of("ok", true, "message", "API de Vinilove en Spring Boot");
	}
}
