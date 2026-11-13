package com.vinilove.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "pedidos")
public class Pedido {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "usuario_id")
	private Usuario usuario;

	@Column(name = "nombre_entrega", nullable = false, length = 150)
	private String nombreEntrega;

	@Column(nullable = false, length = 255)
	private String direccion;

	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal total;

	@Column(nullable = false, length = 30)
	private String estado;

	@Column(name = "creado_en")
	private LocalDateTime creadoEn;

	@OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PedidoItem> items = new ArrayList<>();

	@PrePersist
	void marcarFecha() {
		if (creadoEn == null) {
			creadoEn = LocalDateTime.now();
		}
	}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public Usuario getUsuario() { return usuario; }
	public void setUsuario(Usuario usuario) { this.usuario = usuario; }
	public String getNombreEntrega() { return nombreEntrega; }
	public void setNombreEntrega(String nombreEntrega) { this.nombreEntrega = nombreEntrega; }
	public String getDireccion() { return direccion; }
	public void setDireccion(String direccion) { this.direccion = direccion; }
	public BigDecimal getTotal() { return total; }
	public void setTotal(BigDecimal total) { this.total = total; }
	public String getEstado() { return estado; }
	public void setEstado(String estado) { this.estado = estado; }
	public LocalDateTime getCreadoEn() { return creadoEn; }
	public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }
	public List<PedidoItem> getItems() { return items; }
	public void setItems(List<PedidoItem> items) { this.items = items; }
}
