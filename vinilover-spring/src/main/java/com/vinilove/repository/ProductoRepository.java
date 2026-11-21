package com.vinilove.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vinilove.model.Producto;

public interface ProductoRepository extends JpaRepository<Producto, String> {
	List<Producto> findAllByOrderByNombreAsc();
}
