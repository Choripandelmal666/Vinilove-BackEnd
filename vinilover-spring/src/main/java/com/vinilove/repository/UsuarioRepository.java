package com.vinilove.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.vinilove.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
	Optional<Usuario> findByCorreoIgnoreCase(String correo);
	boolean existsByCorreoIgnoreCase(String correo);
	boolean existsByNombreUsuarioIgnoreCase(String nombreUsuario);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query(value = """
			INSERT INTO usuarios (id, run, correo, nombre, apellidos, password, rol, nombre_usuario)
			VALUES (:id, :run, :correo, :nombre, '', 'sincronizado', 'cliente', :nombreUsuario)
			""", nativeQuery = true)
	void insertarComprador(@Param("id") Long id, @Param("run") String run, @Param("correo") String correo,
			@Param("nombre") String nombre, @Param("nombreUsuario") String nombreUsuario);
}
