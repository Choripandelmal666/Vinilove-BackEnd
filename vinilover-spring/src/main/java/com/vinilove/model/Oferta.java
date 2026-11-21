package com.vinilove.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ofertas")
public class Oferta {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(optional = false)
	@JoinColumn(name = "producto_id", unique = true)
	private Producto producto;

	@Column(name = "precio_oferta", nullable = false, precision = 10, scale = 2)
	private BigDecimal precioOferta;

	@Column(nullable = false)
	private Integer porcentaje;

	@Column(nullable = false)
	private boolean activa;

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public Producto getProducto() { return producto; }
	public void setProducto(Producto producto) { this.producto = producto; }
	public BigDecimal getPrecioOferta() { return precioOferta; }
	public void setPrecioOferta(BigDecimal precioOferta) { this.precioOferta = precioOferta; }
	public Integer getPorcentaje() { return porcentaje; }
	public void setPorcentaje(Integer porcentaje) { this.porcentaje = porcentaje; }
	public boolean isActiva() { return activa; }
	public void setActiva(boolean activa) { this.activa = activa; }
}
