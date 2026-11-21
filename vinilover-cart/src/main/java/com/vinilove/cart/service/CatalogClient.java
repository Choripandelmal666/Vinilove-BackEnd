package com.vinilove.cart.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import com.vinilove.cart.CartDtos.ProductoResponse;

@Component
public class CatalogClient {

	private final RestClient restClient;

	public CatalogClient(@Value("${catalog.base-url}") String baseUrl) {
		this.restClient = RestClient.builder().baseUrl(baseUrl).build();
	}

	public ProductoResponse buscar(String productoId) {
		try {
			ProductoResponse producto = restClient.get()
					.uri("/api/productos/{id}", productoId)
					.retrieve()
					.body(ProductoResponse.class);
			if (producto == null) {
				throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Catálogo devolvió una respuesta vacía.");
			}
			return producto;
		} catch (HttpClientErrorException.NotFound ex) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Producto no encontrado: " + productoId);
		}
	}
}
