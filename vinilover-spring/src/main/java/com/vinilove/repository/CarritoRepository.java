package com.vinilove.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vinilove.model.Carrito;

public interface CarritoRepository extends JpaRepository<Carrito, Long> {
	Optional<Carrito> findByUsuario_Id(Long usuarioId);
}
