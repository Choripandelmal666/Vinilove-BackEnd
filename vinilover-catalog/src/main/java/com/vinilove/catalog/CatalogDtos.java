package com.vinilove.catalog;

import java.math.BigDecimal;

public final class CatalogDtos {

	private CatalogDtos() {}

	public record ProductoRequest(
			String id,
			String nombre,
			String artista,
			BigDecimal precio,
			String anio,
			String categoria,
			Boolean oferta,
			String imagen,
			String descripcion,
			Long vendedorId) {}

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
			String tienda) {}

	public record OfertaRequest(String productoId, Integer porcentaje, BigDecimal precioOferta) {}

	public record OfertaResponse(
			Long id,
			String productoId,
			String nombre,
			BigDecimal precio,
			BigDecimal precioOferta,
			Integer porcentaje,
			boolean activa) {}
}
