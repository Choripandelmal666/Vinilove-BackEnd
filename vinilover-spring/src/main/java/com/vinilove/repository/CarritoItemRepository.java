package com.vinilove.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vinilove.model.CarritoItem;

public interface CarritoItemRepository extends JpaRepository<CarritoItem, Long> {
	void deleteByProducto_Id(String productoId);
}
