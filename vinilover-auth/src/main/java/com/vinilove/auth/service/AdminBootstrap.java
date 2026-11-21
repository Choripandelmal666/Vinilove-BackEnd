package com.vinilove.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.vinilove.auth.model.Usuario;
import com.vinilove.auth.repository.UsuarioRepository;

@Component
public class AdminBootstrap implements CommandLineRunner {

	private final UsuarioRepository usuarios;
	private final PasswordEncoder passwordEncoder;
	private final String correo;
	private final String password;
	private final String run;

	public AdminBootstrap(
			UsuarioRepository usuarios,
			PasswordEncoder passwordEncoder,
			@Value("${vinilove.auth.admin.email:}") String correo,
			@Value("${vinilove.auth.admin.password:}") String password,
			@Value("${vinilove.auth.admin.run:ADMIN0001}") String run) {
		this.usuarios = usuarios;
		this.passwordEncoder = passwordEncoder;
		this.correo = correo;
		this.password = password;
		this.run = run;
	}

	@Override
	public void run(String... args) {
		if (correo.isBlank() || password.length() < 12) {
			return;
		}
		java.util.Optional<Usuario> existente = usuarios.findByCorreoIgnoreCase(correo);
		if (existente.isPresent()) {
			Usuario admin = existente.get();
			if (!"admin".equalsIgnoreCase(admin.rol)) {
				admin.rol = "admin";
			}
			if (!passwordEncoder.matches(password, admin.password)) {
				admin.password = passwordEncoder.encode(password);
			}
			admin.nombreUsuario = "admin";
			usuarios.save(admin);
			return;
		}
		Usuario admin = new Usuario();
		admin.run = run;
		admin.correo = correo;
		admin.nombre = "Administrador";
		admin.apellidos = "Vinilove";
		admin.password = passwordEncoder.encode(password);
		admin.rol = "admin";
		admin.nombreUsuario = "admin";
		usuarios.save(admin);
	}
}