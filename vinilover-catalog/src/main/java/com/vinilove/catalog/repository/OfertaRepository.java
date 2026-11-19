package com.vinilove.catalog.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vinilove.catalog.model.Oferta;

public interface OfertaRepository extends JpaRepository<Oferta, Long> {

	Optional<Oferta> findByProducto_Id(String productoId);

	void deleteByProducto_Id(String productoId);
}
