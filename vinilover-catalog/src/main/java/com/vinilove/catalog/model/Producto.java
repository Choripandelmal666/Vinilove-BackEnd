package com.vinilove.catalog.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "productos")
public class Producto {

	@Id
	@Column(length = 120)
	public String id;

	@Column(nullable = false, length = 150)
	public String nombre;

	@Column(nullable = false, length = 150)
	public String artista;

	@Column(nullable = false, precision = 10, scale = 2)
	public BigDecimal precio;

	@Column(length = 10)
	public String anio;

	@Column(nullable = false, length = 60)
	public String categoria;

	@Column(nullable = false, columnDefinition = "LONGTEXT")
	public String imagen;

	@Column(columnDefinition = "TEXT")
	public String descripcion;

	@Column(name = "vendedor_id")
	public Long vendedorId;
}
