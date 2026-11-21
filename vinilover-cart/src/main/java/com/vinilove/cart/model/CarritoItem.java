package com.vinilove.cart.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "carrito_items", uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id", "producto_id"}))
public class CarritoItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long id;

	@Column(name = "usuario_id", nullable = false)
	public Long usuarioId;

	@Column(name = "producto_id", nullable = false, length = 120)
	public String productoId;

	@Column(nullable = false)
	public int cantidad;
}
