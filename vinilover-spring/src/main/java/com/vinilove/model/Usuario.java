package com.vinilove.model;

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
	private Long id;

	@Column(nullable = false, unique = true, length = 12)
	private String run;

	@Column(nullable = false, unique = true, length = 100)
	private String correo;

	@Column(nullable = false, length = 100)
	private String nombre;

	@Column(length = 100)
	private String apellidos;

	@Column(nullable = false, length = 20)
	private String password;

	@Column(nullable = false, length = 20)
	private String rol;

	@Column(name = "nombre_usuario", unique = true, length = 40)
	private String nombreUsuario;

	@Column(name = "foto_perfil", columnDefinition = "LONGTEXT")
	private String fotoPerfil;

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public String getRun() { return run; }
	public void setRun(String run) { this.run = run; }
	public String getCorreo() { return correo; }
	public void setCorreo(String correo) { this.correo = correo; }
	public String getNombre() { return nombre; }
	public void setNombre(String nombre) { this.nombre = nombre; }
	public String getApellidos() { return apellidos; }
	public void setApellidos(String apellidos) { this.apellidos = apellidos; }
	public String getPassword() { return password; }
	public void setPassword(String password) { this.password = password; }
	public String getRol() { return rol; }
	public void setRol(String rol) { this.rol = rol; }
	public String getNombreUsuario() { return nombreUsuario; }
	public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }
	public String getFotoPerfil() { return fotoPerfil; }
	public void setFotoPerfil(String fotoPerfil) { this.fotoPerfil = fotoPerfil; }
}
