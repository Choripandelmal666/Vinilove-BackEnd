package com.vinilove.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "pedido_items")
public class PedidoItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "pedido_id")
	private Pedido pedido;

	@Column(name = "producto_id", length = 120)
	private String productoId;

	@Column(nullable = false, length = 150)
	private String nombre;

	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal precio;

	@Column(nullable = false)
	private int cantidad;

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public Pedido getPedido() { return pedido; }
	public void setPedido(Pedido pedido) { this.pedido = pedido; }
	public String getProductoId() { return productoId; }
	public void setProductoId(String productoId) { this.productoId = productoId; }
	public String getNombre() { return nombre; }
	public void setNombre(String nombre) { this.nombre = nombre; }
	public BigDecimal getPrecio() { return precio; }
	public void setPrecio(BigDecimal precio) { this.precio = precio; }
	public int getCantidad() { return cantidad; }
	public void setCantidad(int cantidad) { this.cantidad = cantidad; }
}
