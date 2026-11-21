package com.vinilove.cart.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vinilove.cart.model.CarritoItem;

public interface CarritoItemRepository extends JpaRepository<CarritoItem, Long> {

	List<CarritoItem> findByUsuarioIdOrderById(Long usuarioId);

	void deleteByUsuarioId(Long usuarioId);
}
