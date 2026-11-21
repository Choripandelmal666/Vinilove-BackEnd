package com.vinilove.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "productos")
public class Producto {

	@Id
	@Column(length = 120)
	private String id;

	@Column(nullable = false, length = 150)
	private String nombre;

	@Column(nullable = false, length = 150)
	private String artista;

	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal precio;

	@Column(length = 10)
	private String anio;

	@Column(nullable = false, length = 60)
	private String categoria;

	@Column(nullable = false, columnDefinition = "LONGTEXT")
	private String imagen;

	@Column(columnDefinition = "TEXT")
	private String descripcion;

	@ManyToOne
	@JoinColumn(name = "vendedor_id")
	private Usuario vendedor;

	public String getId() { return id; }
	public void setId(String id) { this.id = id; }
	public String getNombre() { return nombre; }
	public void setNombre(String nombre) { this.nombre = nombre; }
	public String getArtista() { return artista; }
	public void setArtista(String artista) { this.artista = artista; }
	public BigDecimal getPrecio() { return precio; }
	public void setPrecio(BigDecimal precio) { this.precio = precio; }
	public String getAnio() { return anio; }
	public void setAnio(String anio) { this.anio = anio; }
	public String getCategoria() { return categoria; }
	public void setCategoria(String categoria) { this.categoria = categoria; }
	public String getImagen() { return imagen; }
	public void setImagen(String imagen) { this.imagen = imagen; }
	public String getDescripcion() { return descripcion; }
	public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
	public Usuario getVendedor() { return vendedor; }
	public void setVendedor(Usuario vendedor) { this.vendedor = vendedor; }
}
