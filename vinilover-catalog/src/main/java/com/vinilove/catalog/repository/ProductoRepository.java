package com.vinilove.catalog.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vinilove.catalog.model.Producto;

public interface ProductoRepository extends JpaRepository<Producto, String> {

	List<Producto> findAllByOrderByNombreAsc();
}
