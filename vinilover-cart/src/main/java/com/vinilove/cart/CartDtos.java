package com.vinilove.cart;

import java.math.BigDecimal;

public final class CartDtos {

	private CartDtos() {}

	public record ItemRequest(String productoId, String id, String nombre, BigDecimal precio, Integer cantidad) {}

	public record ItemResponse(String id, String nombre, String artista, BigDecimal precio, int cantidad, String imagen) {}

	public record ProductoResponse(
			String id,
			String nombre,
			String artista,
			BigDecimal precio,
			String anio,
			String categoria,
			boolean oferta,
			BigDecimal precioOferta,
			Integer porcentaje,
			String imagen,
			String descripcion,
			Long vendedorId,
			String tienda) {
		public BigDecimal precioCobrado() {
			return oferta && precioOferta != null ? precioOferta : precio;
		}
	}
}
