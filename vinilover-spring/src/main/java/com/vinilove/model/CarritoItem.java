package com.vinilove.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "carrito_items", uniqueConstraints = @UniqueConstraint(columnNames = {"carrito_id", "producto_id"}))
public class CarritoItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "carrito_id")
	private Carrito carrito;

	@ManyToOne(optional = false)
	@JoinColumn(name = "producto_id")
	private Producto producto;

	private int cantidad;

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public Carrito getCarrito() { return carrito; }
	public void setCarrito(Carrito carrito) { this.carrito = carrito; }
	public Producto getProducto() { return producto; }
	public void setProducto(Producto producto) { this.producto = producto; }
	public int getCantidad() { return cantidad; }
	public void setCantidad(int cantidad) { this.cantidad = cantidad; }
}
