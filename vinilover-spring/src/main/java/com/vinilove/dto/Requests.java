package com.vinilove.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Requests {

	public static class ProductoRequest {
		public String id;
		public String nombre;
		public String artista;
		public BigDecimal precio;
		public String anio;
		public String categoria;
		public Boolean oferta;
		public String imagen;
		public String descripcion;
		public Long vendedorId;
	}

	public static class RegistroRequest {
		public String run;
		public String correo;
		public String nombre;
		public String apellidos;
		public String password;
		public String rol;
		public String nombreUsuario;
		public String nombreTienda;
		public String descripcion;
		public String ciudad;
	}

	public static class LoginRequest {
		public String correo;
		public String password;
	}

	public static class PerfilRequest {
		public String nombreUsuario;
		public String fotoPerfil;
	}

	public static class BlogRequest {
		public String id;
		public String categoria;
		public String titulo;
		public String extracto;
		public String contenidoCompleto;
		public String imagen;
		public String enlaceProducto;
		public String fecha;
	}

	public static class VendedorRequest {
		public String nombreTienda;
		public String descripcion;
		public String ciudad;
	}

	public static class OfertaRequest {
		public String productoId;
		public Integer porcentaje;
		public BigDecimal precioOferta;
	}

	public static class ItemRequest {
		public String productoId;
		public String id;
		public String nombre;
		public BigDecimal precio;
		public Integer cantidad;
	}

	public static class PedidoRequest {
		public Long usuarioId;
		public String nombre;
		public String direccion;
		public List<ItemRequest> items = new ArrayList<>();
	}

	public static class ContactoRequest {
		public String nombre;
		public String correo;
		public String comentario;
	}
}
