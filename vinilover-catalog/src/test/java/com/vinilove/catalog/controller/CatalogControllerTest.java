package com.vinilove.catalog.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.vinilove.catalog.model.Producto;
import com.vinilove.catalog.repository.OfertaRepository;
import com.vinilove.catalog.repository.ProductoRepository;

class CatalogControllerTest {

	private ProductoRepository productos;
	private OfertaRepository ofertas;
	private CatalogController controller;

	@BeforeEach
	void preparar() {
		productos = org.mockito.Mockito.mock(ProductoRepository.class);
		ofertas = org.mockito.Mockito.mock(OfertaRepository.class);
		controller = new CatalogController(productos, ofertas);
	}

	@Test
	void listaUnDisco() {
		Producto disco = new Producto();
		disco.id = "dirt";
		disco.nombre = "Dirt";
		disco.artista = "Alice in Chains";
		disco.precio = new BigDecimal("31990");
		disco.categoria = "Grunge";
		disco.imagen = "/dirt.jpg";
		when(productos.findAllByOrderByNombreAsc()).thenReturn(List.of(disco));
		when(ofertas.findByProducto_Id("dirt")).thenReturn(Optional.empty());

		var lista = controller.listarProductos();

		assertEquals(1, lista.size());
		assertEquals("Dirt", lista.get(0).nombre());
	}

	@Test
	void discoInexistenteFalla() {
		when(productos.findById("no-existe")).thenReturn(Optional.empty());
		ResponseStatusException error = assertThrows(ResponseStatusException.class,
				() -> controller.buscarProducto("no-existe"));
		assertEquals(HttpStatus.NOT_FOUND, error.getStatusCode());
	}
}