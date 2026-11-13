package com.vinilove.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Responses {

	public static class ProductoResponse {
		public String id;
		public String nombre;
		public String artista;
		public BigDecimal precio;
		public String anio;
		public String categoria;
		public boolean oferta;
		public BigDecimal precioOferta;
		public Integer porcentaje;
		public String imagen;
		public String descripcion;
		public Long vendedorId;
		public String tienda;
	}

	public static class OfertaResponse {
		public Long id;
		public String productoId;
		public String nombre;
		public BigDecimal precio;
		public BigDecimal precioOferta;
		public Integer porcentaje;
		public boolean activa;
	}

	public static class UsuarioResponse {
		public Long id;
		public String run;
		public String correo;
		public String nombre;
		public String apellidos;
		public String rol;
		public String nombreUsuario;
		public String fotoPerfil;
		public String nombreTienda;
		public String descripcionTienda;
		public String ciudad;
	}

	public static class CarritoItemResponse {
		public String id;
		public String nombre;
		public String artista;
		public BigDecimal precio;
		public int cantidad;
		public String imagen;
	}

	public static class PedidoItemResponse {
		public String productoId;
		public String nombre;
		public BigDecimal precio;
		public int cantidad;
	}

	public static class PedidoResponse {
		public Long id;
		public Long usuarioId;
		public String nombre;
		public String direccion;
		public BigDecimal total;
		public String estado;
		public LocalDateTime creadoEn;
		public List<PedidoItemResponse> items = new ArrayList<>();
	}

	public static class MensajeResponse {
		public Long id;
		public String nombre;
		public String correo;
		public String comentario;
		public LocalDateTime creadoEn;
	}

	public static class BlogResponse {
		public String id;
		public String categoria;
		public String titulo;
		public String extracto;
		public String contenidoCompleto;
		public String imagen;
		public String enlaceProducto;
		public String fecha;
	}
}
