package com.vinilove.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vinilove.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
	Optional<Usuario> findByCorreoIgnoreCase(String correo);
	boolean existsByCorreoIgnoreCase(String correo);
	boolean existsByNombreUsuarioIgnoreCase(String nombreUsuario);
}
