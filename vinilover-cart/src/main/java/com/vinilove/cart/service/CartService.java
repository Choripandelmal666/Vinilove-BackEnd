package com.vinilove.cart.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vinilove.cart.CartDtos.ItemRequest;
import com.vinilove.cart.CartDtos.ItemResponse;
import com.vinilove.cart.CartDtos.ProductoResponse;
import com.vinilove.cart.model.CarritoItem;
import com.vinilove.cart.repository.CarritoItemRepository;

@Service
public class CartService {

	private final CarritoItemRepository items;
	private final CatalogClient catalogo;

	public CartService(CarritoItemRepository items, CatalogClient catalogo) {
		this.items = items;
		this.catalogo = catalogo;
	}

	@Transactional(readOnly = true)
	public List<ItemResponse> ver(Long usuarioId) {
		return items.findByUsuarioIdOrderById(usuarioId).stream().map(this::respuesta).toList();
	}

	@Transactional
	public List<ItemResponse> reemplazar(Long usuarioId, List<ItemRequest> datos) {
		Map<String, Integer> cantidades = new LinkedHashMap<>();
		if (datos != null) {
			for (ItemRequest item : datos) {
				if (item.cantidad() == null || item.cantidad() < 1) {
					continue;
				}
				String productoId = item.productoId() == null || item.productoId().isBlank() ? item.id() : item.productoId();
				if (productoId != null && !productoId.isBlank()) {
					cantidades.merge(productoId, item.cantidad(), Integer::sum);
				}
			}
		}

		Map<String, ProductoResponse> productos = new LinkedHashMap<>();
		cantidades.keySet().forEach(id -> productos.put(id, catalogo.buscar(id)));
		items.deleteByUsuarioId(usuarioId);
		for (Map.Entry<String, Integer> entrada : cantidades.entrySet()) {
			CarritoItem item = new CarritoItem();
			item.usuarioId = usuarioId;
			item.productoId = entrada.getKey();
			item.cantidad = entrada.getValue();
			items.save(item);
		}
		return cantidades.entrySet().stream()
				.map(entrada -> respuesta(productos.get(entrada.getKey()), entrada.getValue()))
				.toList();
	}

	private ItemResponse respuesta(CarritoItem item) {
		return respuesta(catalogo.buscar(item.productoId), item.cantidad);
	}

	private ItemResponse respuesta(ProductoResponse producto, int cantidad) {
		return new ItemResponse(producto.id(), producto.nombre(), producto.artista(), producto.precioCobrado(),
				cantidad, producto.imagen());
	}
}
