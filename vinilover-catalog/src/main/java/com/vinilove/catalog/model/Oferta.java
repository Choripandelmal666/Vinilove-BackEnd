package com.vinilove.catalog.model;

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
	public Long id;

	@OneToOne(optional = false)
	@JoinColumn(name = "producto_id", nullable = false, unique = true)
	public Producto producto;

	@Column(name = "precio_oferta", nullable = false, precision = 10, scale = 2)
	public BigDecimal precioOferta;

	@Column(nullable = false)
	public Integer porcentaje;

	@Column(nullable = false)
	public boolean activa;
}
