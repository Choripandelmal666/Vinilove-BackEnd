package com.vinilove.auth.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuarios")
public class Usuario {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long id;

	@Column(nullable = false, unique = true, length = 12)
	public String run;

	@Column(nullable = false, unique = true, length = 100)
	public String correo;

	@Column(nullable = false, length = 100)
	public String nombre;

	@Column(length = 100)
	public String apellidos;

	@Column(nullable = false, length = 100)
	public String password;

	@Column(nullable = false, length = 20)
	public String rol;

	@Column(name = "nombre_usuario", unique = true, length = 40)
	public String nombreUsuario;

	@Column(name = "foto_perfil", columnDefinition = "LONGTEXT")
	public String fotoPerfil;

	@Column(name = "nombre_tienda", length = 150)
	public String nombreTienda;

	@Column(name = "descripcion_tienda", columnDefinition = "TEXT")
	public String descripcionTienda;

	@Column(length = 80)
	public String ciudad;
}
