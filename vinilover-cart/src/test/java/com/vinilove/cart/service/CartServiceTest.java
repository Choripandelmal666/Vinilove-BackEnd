package com.vinilove.cart.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vinilove.cart.CartDtos.ItemRequest;
import com.vinilove.cart.CartDtos.ProductoResponse;
import com.vinilove.cart.repository.CarritoItemRepository;

class CartServiceTest {

	private CarritoItemRepository items;
	private CatalogClient catalogo;
	private CartService servicio;

	@BeforeEach
	void preparar() {
		items = org.mockito.Mockito.mock(CarritoItemRepository.class);
		catalogo = org.mockito.Mockito.mock(CatalogClient.class);
		servicio = new CartService(items, catalogo);
		when(catalogo.buscar("dirt")).thenReturn(new ProductoResponse(
				"dirt", "Dirt", "Alice in Chains", new BigDecimal("31990"),
				"1992", "Grunge", false, null, null, "/dirt.jpg", "", null, null));
	}

	@Test
	void juntaDosUnidadesDelMismoDisco() {
		var resultado = servicio.reemplazar(5L, List.of(
				new ItemRequest("dirt", null, null, null, 1),
				new ItemRequest("dirt", null, null, null, 1)));
		assertEquals(1, resultado.size());
		assertEquals(2, resultado.get(0).cantidad());
		verify(items).deleteByUsuarioId(5L);
	}

	@Test
	void cantidadCeroNoSeGuarda() {
		var resultado = servicio.reemplazar(5L, List.of(new ItemRequest("dirt", null, null, null, 0)));
		assertEquals(0, resultado.size());
		verify(catalogo, never()).buscar(any());
	}
}