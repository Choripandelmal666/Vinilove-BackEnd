package com.vinilove.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "perfiles_vendedor")
public class PerfilVendedor {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(optional = false)
	@JoinColumn(name = "usuario_id", unique = true)
	private Usuario usuario;

	@Column(name = "nombre_tienda", nullable = false, length = 150)
	private String nombreTienda;

	@Column(columnDefinition = "TEXT")
	private String descripcion;

	@Column(length = 80)
	private String ciudad;

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public Usuario getUsuario() { return usuario; }
	public void setUsuario(Usuario usuario) { this.usuario = usuario; }
	public String getNombreTienda() { return nombreTienda; }
	public void setNombreTienda(String nombreTienda) { this.nombreTienda = nombreTienda; }
	public String getDescripcion() { return descripcion; }
	public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
	public String getCiudad() { return ciudad; }
	public void setCiudad(String ciudad) { this.ciudad = ciudad; }
}
