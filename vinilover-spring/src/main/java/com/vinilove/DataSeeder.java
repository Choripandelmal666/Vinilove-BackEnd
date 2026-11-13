package com.vinilove;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.vinilove.service.ViniloveService;

@Component
public class DataSeeder implements CommandLineRunner {

	private final ViniloveService viniloveService;

	public DataSeeder(ViniloveService viniloveService) {
		this.viniloveService = viniloveService;
	}

	@Override
	public void run(String... args) {
		viniloveService.sembrarSiFalta();
	}
}
