package com.vinilove.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "mensajes_contacto")
public class MensajeContacto {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 100)
	private String nombre;

	@Column(length = 100)
	private String correo;

	@Column(nullable = false, length = 500)
	private String comentario;

	@Column(name = "creado_en")
	private LocalDateTime creadoEn;

	@PrePersist
	void marcarFecha() {
		if (creadoEn == null) {
			creadoEn = LocalDateTime.now();
		}
	}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public String getNombre() { return nombre; }
	public void setNombre(String nombre) { this.nombre = nombre; }
	public String getCorreo() { return correo; }
	public void setCorreo(String correo) { this.correo = correo; }
	public String getComentario() { return comentario; }
	public void setComentario(String comentario) { this.comentario = comentario; }
	public LocalDateTime getCreadoEn() { return creadoEn; }
	public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }
}
