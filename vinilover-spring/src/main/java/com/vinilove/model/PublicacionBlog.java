package com.vinilove.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "publicaciones_blog")
public class PublicacionBlog {

	@Id
	@Column(length = 120)
	private String id;

	@Column(nullable = false, length = 60)
	private String categoria;

	@Column(nullable = false, length = 200)
	private String titulo;

	@Column(columnDefinition = "TEXT")
	private String extracto;

	@Column(name = "contenido_completo", columnDefinition = "TEXT")
	private String contenidoCompleto;

	@Column(columnDefinition = "LONGTEXT")
	private String imagen;

	@Column(name = "enlace_producto", length = 255)
	private String enlaceProducto;

	@Column(length = 80)
	private String fecha;

	public String getId() { return id; }
	public void setId(String id) { this.id = id; }
	public String getCategoria() { return categoria; }
	public void setCategoria(String categoria) { this.categoria = categoria; }
	public String getTitulo() { return titulo; }
	public void setTitulo(String titulo) { this.titulo = titulo; }
	public String getExtracto() { return extracto; }
	public void setExtracto(String extracto) { this.extracto = extracto; }
	public String getContenidoCompleto() { return contenidoCompleto; }
	public void setContenidoCompleto(String contenidoCompleto) { this.contenidoCompleto = contenidoCompleto; }
	public String getImagen() { return imagen; }
	public void setImagen(String imagen) { this.imagen = imagen; }
	public String getEnlaceProducto() { return enlaceProducto; }
	public void setEnlaceProducto(String enlaceProducto) { this.enlaceProducto = enlaceProducto; }
	public String getFecha() { return fecha; }
	public void setFecha(String fecha) { this.fecha = fecha; }
}
