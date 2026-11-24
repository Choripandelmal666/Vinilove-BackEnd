package com.vinilove.catalog.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.vinilove.catalog.CatalogDtos.OfertaRequest;
import com.vinilove.catalog.CatalogDtos.OfertaResponse;
import com.vinilove.catalog.CatalogDtos.ProductoRequest;
import com.vinilove.catalog.CatalogDtos.ProductoResponse;
import com.vinilove.catalog.model.Oferta;
import com.vinilove.catalog.model.Producto;
import com.vinilove.catalog.repository.OfertaRepository;
import com.vinilove.catalog.repository.ProductoRepository;

@RestController
@RequestMapping("/api")
@Transactional
public class CatalogController {

	private final ProductoRepository productos;
	private final OfertaRepository ofertas;

	public CatalogController(ProductoRepository productos, OfertaRepository ofertas) {
		this.productos = productos;
		this.ofertas = ofertas;
	}

	@GetMapping("/productos")
	public List<ProductoResponse> listarProductos() {
		return productos.findAllByOrderByNombreAsc().stream().map(this::respuesta).toList();
	}

	@GetMapping("/productos/{id}")
	public ProductoResponse buscarProducto(@PathVariable String id) {
		return respuesta(productoObligatorio(id));
	}

	@PostMapping("/productos")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ProductoResponse> crearProducto(@RequestBody ProductoRequest datos, @AuthenticationPrincipal Jwt jwt) {
		Producto producto = new Producto();
		producto.id = datos.id() == null || datos.id().isBlank() ? slug(datos.nombre()) + "-" + System.currentTimeMillis() : datos.id();
		if (productos.existsById(producto.id)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Ese producto ya existe.");
		}
		copiar(producto, datos);
		if (!esAdmin(jwt)) {
			producto.vendedorId = usuarioId(jwt);
		}
		productos.save(producto);
		guardarOferta(producto, Boolean.TRUE.equals(datos.oferta()), datos.precio());
		return ResponseEntity.created(URI.create("/api/productos/" + producto.id)).body(respuesta(producto));
	}

	@PutMapping("/productos/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ProductoResponse editarProducto(@PathVariable String id, @RequestBody ProductoRequest datos,
			@AuthenticationPrincipal Jwt jwt) {
		Producto producto = productoObligatorio(id);
		validarPropietario(producto, jwt);
		copiar(producto, datos);
		if (!esAdmin(jwt)) {
			producto.vendedorId = usuarioId(jwt);
		}
		guardarOferta(producto, Boolean.TRUE.equals(datos.oferta()), datos.precio());
		return respuesta(producto);
	}

	@DeleteMapping("/productos/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Void> eliminarProducto(@PathVariable String id, @AuthenticationPrincipal Jwt jwt) {
		Producto producto = productoObligatorio(id);
		validarPropietario(producto, jwt);
		ofertas.deleteByProducto_Id(id);
		productos.delete(producto);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/ofertas")
	public List<ProductoResponse> listarOfertas() {
		return productos.findAllByOrderByNombreAsc().stream()
				.filter(producto -> ofertas.findByProducto_Id(producto.id).filter(oferta -> oferta.activa).isPresent())
				.map(this::respuesta).toList();
	}

	@PostMapping("/ofertas")
	@PreAuthorize("hasRole('ADMIN')")
	public OfertaResponse crearOferta(@RequestBody OfertaRequest datos, @AuthenticationPrincipal Jwt jwt) {
		Producto producto = productoObligatorio(datos.productoId());
		validarPropietario(producto, jwt);
		int porcentaje = datos.porcentaje() == null ? 10 : datos.porcentaje();
		Oferta oferta = ofertas.findByProducto_Id(producto.id).orElseGet(Oferta::new);
		oferta.producto = producto;
		oferta.porcentaje = porcentaje;
		oferta.precioOferta = datos.precioOferta() == null ? descontar(producto.precio, porcentaje) : datos.precioOferta();
		oferta.activa = true;
		return respuesta(ofertas.save(oferta));
	}

	@DeleteMapping("/ofertas/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Void> eliminarOferta(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
		Oferta oferta = ofertas.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Oferta no encontrada."));
		validarPropietario(oferta.producto, jwt);
		oferta.activa = false;
		return ResponseEntity.noContent().build();
	}

	private Producto productoObligatorio(String id) {
		return productos.findById(id == null ? "" : id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado."));
	}

	private void validarPropietario(Producto producto, Jwt jwt) {
		if (!esAdmin(jwt) && (producto.vendedorId == null || !producto.vendedorId.equals(usuarioId(jwt)))) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo puedes modificar tus propios productos.");
		}
	}

	private boolean esAdmin(Jwt jwt) {
		return "ADMIN".equals(jwt.getClaimAsString("role"));
	}

	private Long usuarioId(Jwt jwt) {
		return Long.valueOf(jwt.getSubject());
	}

	private void copiar(Producto producto, ProductoRequest datos) {
		producto.nombre = texto(datos.nombre(), "Sin nombre");
		producto.artista = texto(datos.artista(), "Sin artista");
		producto.precio = datos.precio() == null ? BigDecimal.ZERO : datos.precio();
		producto.anio = texto(datos.anio(), "2024");
		producto.categoria = texto(datos.categoria(), "Rock");
		producto.imagen = texto(datos.imagen(), "/imagenes/default.jpg");
		producto.descripcion = texto(datos.descripcion(), "Producto agregado desde la API.");
		producto.vendedorId = datos.vendedorId();
	}

	private void guardarOferta(Producto producto, boolean activa, BigDecimal precioBase) {
		Oferta oferta = ofertas.findByProducto_Id(producto.id).orElse(null);
		if (!activa) {
			if (oferta != null) {
				oferta.activa = false;
			}
			return;
		}
		if (oferta == null) {
			oferta = new Oferta();
			oferta.producto = producto;
			oferta.porcentaje = 15;
		}
		oferta.precioOferta = descontar(precioBase == null ? producto.precio : precioBase, oferta.porcentaje);
		oferta.activa = true;
		ofertas.save(oferta);
	}

	private ProductoResponse respuesta(Producto producto) {
		Oferta oferta = ofertas.findByProducto_Id(producto.id).orElse(null);
		boolean activa = oferta != null && oferta.activa;
		return new ProductoResponse(producto.id, producto.nombre, producto.artista, producto.precio, producto.anio,
				producto.categoria, activa, activa ? oferta.precioOferta : null, activa ? oferta.porcentaje : null,
				producto.imagen, producto.descripcion, producto.vendedorId, null);
	}

	private OfertaResponse respuesta(Oferta oferta) {
		return new OfertaResponse(oferta.id, oferta.producto.id, oferta.producto.nombre, oferta.producto.precio,
				oferta.precioOferta, oferta.porcentaje, oferta.activa);
	}

	private BigDecimal descontar(BigDecimal precio, int porcentaje) {
		return precio.multiply(BigDecimal.valueOf(100 - porcentaje)).divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP);
	}

	private String texto(String actual, String porDefecto) {
		return actual == null || actual.isBlank() ? porDefecto : actual.trim();
	}

	private String slug(String nombre) {
		if (nombre == null || nombre.isBlank()) {
			return "producto";
		}
		return nombre.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
	}
}
