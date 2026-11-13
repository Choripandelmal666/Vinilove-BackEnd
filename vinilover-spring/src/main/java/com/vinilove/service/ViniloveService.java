package com.vinilove.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.vinilove.dto.Requests.BlogRequest;
import com.vinilove.dto.Requests.ContactoRequest;
import com.vinilove.dto.Requests.ItemRequest;
import com.vinilove.dto.Requests.LoginRequest;
import com.vinilove.dto.Requests.OfertaRequest;
import com.vinilove.dto.Requests.PerfilRequest;
import com.vinilove.dto.Requests.PedidoRequest;
import com.vinilove.dto.Requests.ProductoRequest;
import com.vinilove.dto.Requests.RegistroRequest;
import com.vinilove.dto.Requests.VendedorRequest;
import com.vinilove.dto.Responses.BlogResponse;
import com.vinilove.dto.Responses.CarritoItemResponse;
import com.vinilove.dto.Responses.MensajeResponse;
import com.vinilove.dto.Responses.OfertaResponse;
import com.vinilove.dto.Responses.PedidoItemResponse;
import com.vinilove.dto.Responses.PedidoResponse;
import com.vinilove.dto.Responses.ProductoResponse;
import com.vinilove.dto.Responses.UsuarioResponse;
import com.vinilove.model.Carrito;
import com.vinilove.model.CarritoItem;
import com.vinilove.model.MensajeContacto;
import com.vinilove.model.Oferta;
import com.vinilove.model.Pedido;
import com.vinilove.model.PedidoItem;
import com.vinilove.model.PerfilVendedor;
import com.vinilove.model.Producto;
import com.vinilove.model.PublicacionBlog;
import com.vinilove.model.Usuario;
import com.vinilove.repository.CarritoItemRepository;
import com.vinilove.repository.CarritoRepository;
import com.vinilove.repository.MensajeContactoRepository;
import com.vinilove.repository.OfertaRepository;
import com.vinilove.repository.PedidoRepository;
import com.vinilove.repository.PerfilVendedorRepository;
import com.vinilove.repository.ProductoRepository;
import com.vinilove.repository.PublicacionBlogRepository;
import com.vinilove.repository.UsuarioRepository;

@Service
@Transactional
public class ViniloveService {

	private final UsuarioRepository usuarioRepository;
	private final PerfilVendedorRepository perfilRepository;
	private final ProductoRepository productoRepository;
	private final OfertaRepository ofertaRepository;
	private final CarritoRepository carritoRepository;
	private final CarritoItemRepository carritoItemRepository;
	private final PedidoRepository pedidoRepository;
	private final MensajeContactoRepository mensajeRepository;
	private final PublicacionBlogRepository blogRepository;

	public ViniloveService(
			UsuarioRepository usuarioRepository,
			PerfilVendedorRepository perfilRepository,
			ProductoRepository productoRepository,
			OfertaRepository ofertaRepository,
			CarritoRepository carritoRepository,
			CarritoItemRepository carritoItemRepository,
			PedidoRepository pedidoRepository,
			MensajeContactoRepository mensajeRepository,
			PublicacionBlogRepository blogRepository) {
		this.usuarioRepository = usuarioRepository;
		this.perfilRepository = perfilRepository;
		this.productoRepository = productoRepository;
		this.ofertaRepository = ofertaRepository;
		this.carritoRepository = carritoRepository;
		this.carritoItemRepository = carritoItemRepository;
		this.pedidoRepository = pedidoRepository;
		this.mensajeRepository = mensajeRepository;
		this.blogRepository = blogRepository;
	}

	public List<ProductoResponse> listarProductos() {
		return productoRepository.findAllByOrderByNombreAsc().stream().map(this::aProducto).toList();
	}

	public ProductoResponse buscarProducto(String id) {
		return aProducto(productoObligatorio(id));
	}

	public ProductoResponse crearProducto(ProductoRequest datos) {
		String id = datos.id == null || datos.id.isBlank()
				? slug(datos.nombre) + "-" + System.currentTimeMillis()
				: datos.id;
		if (productoRepository.existsById(id)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Ese producto ya existe.");
		}
		Producto producto = new Producto();
		producto.setId(id);
		copiarProducto(producto, datos);
		productoRepository.save(producto);
		guardarOferta(producto, Boolean.TRUE.equals(datos.oferta), datos.precio);
		return aProducto(producto);
	}

	public ProductoResponse editarProducto(String id, ProductoRequest datos) {
		Producto producto = productoObligatorio(id);
		copiarProducto(producto, datos);
		productoRepository.save(producto);
		guardarOferta(producto, Boolean.TRUE.equals(datos.oferta), datos.precio);
		return aProducto(producto);
	}

	public void eliminarProducto(String id) {
		if (!productoRepository.existsById(id)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado para eliminar.");
		}
		ofertaRepository.deleteByProducto_Id(id);
		carritoItemRepository.deleteByProducto_Id(id);
		productoRepository.deleteById(id);
	}

	public List<ProductoResponse> listarOfertas() {
		return productoRepository.findAllByOrderByNombreAsc().stream()
				.map(this::aProducto)
				.filter(producto -> producto.oferta)
				.toList();
	}

	public OfertaResponse crearOferta(OfertaRequest datos) {
		Producto producto = productoObligatorio(datos.productoId);
		int porcentaje = datos.porcentaje == null ? 10 : datos.porcentaje;
		Oferta oferta = ofertaRepository.findByProducto_Id(producto.getId()).orElseGet(Oferta::new);
		oferta.setProducto(producto);
		oferta.setPorcentaje(porcentaje);
		oferta.setPrecioOferta(datos.precioOferta != null ? datos.precioOferta : descontar(producto.getPrecio(), porcentaje));
		oferta.setActiva(true);
		ofertaRepository.save(oferta);
		return aOferta(oferta);
	}

	public void eliminarOferta(Long id) {
		Oferta oferta = ofertaRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Oferta no encontrada."));
		oferta.setActiva(false);
		ofertaRepository.save(oferta);
	}

	public UsuarioResponse registrar(RegistroRequest datos) {
		if (datos.correo == null || datos.password == null || datos.nombre == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Faltan datos del registro.");
		}
		if (usuarioRepository.existsByCorreoIgnoreCase(datos.correo)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Ese correo ya está registrado.");
		}
		String rol = datos.rol == null || datos.rol.isBlank() ? "cliente" : datos.rol.toLowerCase();
		if (!rol.equals("cliente") && !rol.equals("vendedor") && !rol.equals("admin")) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El rol debe ser cliente, vendedor o admin.");
		}
		Usuario usuario = new Usuario();
		usuario.setRun(datos.run);
		usuario.setCorreo(datos.correo.trim());
		usuario.setNombre(datos.nombre.trim());
		usuario.setApellidos(datos.apellidos == null ? "" : datos.apellidos.trim());
		usuario.setPassword(datos.password);
		usuario.setRol(rol);
		usuario.setNombreUsuario(nombreUsuarioLibre(datos.nombreUsuario, datos.nombre));
		usuarioRepository.save(usuario);
		if ("vendedor".equals(rol)) {
			guardarPerfil(usuario, datos.nombreTienda, datos.descripcion, datos.ciudad);
		}
		return aUsuario(usuario);
	}

	public UsuarioResponse login(LoginRequest datos) {
		Usuario usuario = usuarioRepository.findByCorreoIgnoreCase(datos.correo == null ? "" : datos.correo)
				.filter(encontrado -> encontrado.getPassword().equals(datos.password))
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Correo o contraseña incorrectos."));
		return aUsuario(usuario);
	}

	public List<UsuarioResponse> listarUsuarios() {
		return usuarioRepository.findAll().stream().map(this::aUsuario).toList();
	}

	public UsuarioResponse actualizarPerfil(Long id, PerfilRequest datos) {
		Usuario usuario = usuarioObligatorio(id);
		if (datos.nombreUsuario != null && !datos.nombreUsuario.isBlank()) {
			String limpio = datos.nombreUsuario.trim();
			boolean ocupado = usuarioRepository.existsByNombreUsuarioIgnoreCase(limpio)
					&& (usuario.getNombreUsuario() == null || !usuario.getNombreUsuario().equalsIgnoreCase(limpio));
			if (ocupado) {
				throw new ResponseStatusException(HttpStatus.CONFLICT, "Ese nombre de usuario ya está ocupado.");
			}
			usuario.setNombreUsuario(limpio);
		}
		if (datos.fotoPerfil != null && !datos.fotoPerfil.isBlank()) {
			usuario.setFotoPerfil(datos.fotoPerfil);
		}
		usuarioRepository.save(usuario);
		return aUsuario(usuario);
	}

	public void eliminarUsuario(Long id) {
		Usuario usuario = usuarioObligatorio(id);
		if ("admin@vinilove.com".equalsIgnoreCase(usuario.getCorreo())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se puede borrar la cuenta admin principal.");
		}
		perfilRepository.findByUsuario_Id(id).ifPresent(perfilRepository::delete);
		carritoRepository.findByUsuario_Id(id).ifPresent(carrito -> {
			carrito.getItems().clear();
			carritoRepository.saveAndFlush(carrito);
			carritoRepository.delete(carrito);
		});
		for (Producto producto : productoRepository.findAll()) {
			if (producto.getVendedor() != null && id.equals(producto.getVendedor().getId())) {
				producto.setVendedor(null);
			}
		}
		for (Pedido pedido : pedidoRepository.findByUsuario_IdOrderByIdDesc(id)) {
			pedido.setUsuario(null);
		}
		usuarioRepository.delete(usuario);
	}

	public UsuarioResponse guardarVendedor(Long usuarioId, VendedorRequest datos) {
		Usuario usuario = usuarioObligatorio(usuarioId);
		usuario.setRol("vendedor");
		usuarioRepository.save(usuario);
		guardarPerfil(usuario, datos.nombreTienda, datos.descripcion, datos.ciudad);
		return aUsuario(usuario);
	}

	public List<CarritoItemResponse> verCarrito(Long usuarioId) {
		return itemsDe(carritoDe(usuarioObligatorio(usuarioId)));
	}

	public List<CarritoItemResponse> reemplazarCarrito(Long usuarioId, List<ItemRequest> items) {
		Usuario usuario = usuarioObligatorio(usuarioId);
		Carrito carrito = carritoDe(usuario);
		carrito.getItems().clear();
		carritoRepository.saveAndFlush(carrito);
		if (items != null) {
			for (ItemRequest item : items) {
				if (item.cantidad == null || item.cantidad < 1) {
					continue;
				}
				Producto producto = productoObligatorio(idDeItem(item));
				CarritoItem fila = new CarritoItem();
				fila.setCarrito(carrito);
				fila.setProducto(producto);
				fila.setCantidad(item.cantidad);
				carrito.getItems().add(fila);
			}
		}
		carritoRepository.save(carrito);
		return itemsDe(carrito);
	}

	public PedidoResponse crearPedido(PedidoRequest datos) {
		if (datos.nombre == null || datos.nombre.isBlank() || datos.direccion == null || datos.direccion.isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Completa nombre y dirección de entrega.");
		}
		List<ItemRequest> items = datos.items == null ? new ArrayList<>() : datos.items;
		if (items.isEmpty() && datos.usuarioId != null) {
			items = verCarrito(datos.usuarioId).stream().map(fila -> {
				ItemRequest item = new ItemRequest();
				item.productoId = fila.id;
				item.nombre = fila.nombre;
				item.precio = fila.precio;
				item.cantidad = fila.cantidad;
				return item;
			}).toList();
		}
		if (items.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El carrito está vacío.");
		}

		Pedido pedido = new Pedido();
		if (datos.usuarioId != null) {
			pedido.setUsuario(usuarioObligatorio(datos.usuarioId));
		}
		pedido.setNombreEntrega(datos.nombre.trim());
		pedido.setDireccion(datos.direccion.trim());
		pedido.setEstado("pagado");
		BigDecimal total = BigDecimal.ZERO;
		for (ItemRequest item : items) {
			int cantidad = item.cantidad == null ? 1 : item.cantidad;
			BigDecimal precio = item.precio != null ? item.precio : precioCobrado(productoObligatorio(idDeItem(item)));
			PedidoItem fila = new PedidoItem();
			fila.setPedido(pedido);
			fila.setProductoId(idDeItem(item));
			fila.setNombre(item.nombre == null ? idDeItem(item) : item.nombre);
			fila.setPrecio(precio);
			fila.setCantidad(cantidad);
			pedido.getItems().add(fila);
			total = total.add(precio.multiply(BigDecimal.valueOf(cantidad)));
		}
		pedido.setTotal(total);
		pedidoRepository.save(pedido);
		if (datos.usuarioId != null) {
			reemplazarCarrito(datos.usuarioId, List.of());
		}
		return aPedido(pedido);
	}

	public List<PedidoResponse> listarPedidos() {
		return pedidoRepository.findAll().stream().map(this::aPedido).toList();
	}

	public List<PedidoResponse> pedidosDe(Long usuarioId) {
		usuarioObligatorio(usuarioId);
		return pedidoRepository.findByUsuario_IdOrderByIdDesc(usuarioId).stream().map(this::aPedido).toList();
	}

	public MensajeResponse crearMensaje(ContactoRequest datos) {
		if (datos.nombre == null || datos.nombre.isBlank() || datos.comentario == null || datos.comentario.isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nombre y comentario son obligatorios.");
		}
		MensajeContacto mensaje = new MensajeContacto();
		mensaje.setNombre(datos.nombre.trim());
		mensaje.setCorreo(datos.correo == null ? "" : datos.correo.trim());
		mensaje.setComentario(datos.comentario.trim());
		mensajeRepository.save(mensaje);
		return aMensaje(mensaje);
	}

	public List<MensajeResponse> listarMensajes() {
		return mensajeRepository.findAllByOrderByIdDesc().stream().map(this::aMensaje).toList();
	}

	public List<BlogResponse> listarBlog() {
		return blogRepository.findAll().stream().map(this::aBlog).toList();
	}

	public BlogResponse crearBlog(BlogRequest datos) {
		if (datos.titulo == null || datos.titulo.isBlank() || datos.contenidoCompleto == null || datos.contenidoCompleto.isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El post necesita título y contenido.");
		}
		if (datos.imagen == null || datos.imagen.isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El post necesita una imagen.");
		}
		PublicacionBlog post = new PublicacionBlog();
		post.setId(datos.id == null || datos.id.isBlank() ? slug(datos.titulo) + "-" + System.currentTimeMillis() : datos.id);
		post.setCategoria(texto(datos.categoria, "General"));
		post.setTitulo(datos.titulo.trim());
		post.setExtracto(texto(datos.extracto, datos.titulo.trim()));
		post.setContenidoCompleto(datos.contenidoCompleto.trim());
		post.setImagen(datos.imagen);
		post.setEnlaceProducto(datos.enlaceProducto);
		post.setFecha(texto(datos.fecha, "Hoy"));
		blogRepository.save(post);
		return aBlog(post);
	}

	public void eliminarBlog(String id) {
		if (!blogRepository.existsById(id)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post no encontrado.");
		}
		blogRepository.deleteById(id);
	}

	public void sembrarSiFalta() {
		Usuario admin = usuarioRepository.findByCorreoIgnoreCase("admin@vinilove.com").orElseGet(() -> {
			Usuario nuevo = new Usuario();
			nuevo.setRun("19011022K");
			nuevo.setCorreo("admin@vinilove.com");
			nuevo.setNombre("Admin");
			nuevo.setApellidos("Vinilove");
			nuevo.setPassword("admin1234");
			nuevo.setRol("admin");
			nuevo.setNombreUsuario("admin");
			return usuarioRepository.save(nuevo);
		});
		Usuario vendedor = usuarioRepository.findByCorreoIgnoreCase("vendedor@vinilove.com").orElseGet(() -> {
			Usuario nuevo = new Usuario();
			nuevo.setRun("20111222K");
			nuevo.setCorreo("vendedor@vinilove.com");
			nuevo.setNombre("Camila");
			nuevo.setApellidos("Soto");
			nuevo.setPassword("vende1234");
			nuevo.setRol("vendedor");
			nuevo.setNombreUsuario("camila");
			Usuario guardado = usuarioRepository.save(nuevo);
			guardarPerfil(guardado, "Surcos Santiago", "Vinilos de rock, metal y grunge elegidos a mano.", "Santiago");
			return guardado;
		});
		if (perfilRepository.findByUsuario_Id(vendedor.getId()).isEmpty()) {
			guardarPerfil(vendedor, "Surcos Santiago", "Vinilos de rock, metal y grunge elegidos a mano.", "Santiago");
		}

		crearSiFalta("aenima", "Ænima", "TOOL", 34990, "1996", "Metal", "/imagenes/Tool-AEnima.jpg",
				"Segundo álbum de estudio de TOOL. Clásico en vinilo 180g.", vendedor, null);
		crearSiFalta("jar-of-flies", "Jar of Flies", "Alice in Chains", 28990, "1994", "Grunge", "/imagenes/AliceInChains-JarOfflies.jpg",
				"EP icónico. Primer EP en llegar al número 1 del Billboard 200.", vendedor, 15);
		crearSiFalta("dirt", "Dirt", "Alice in Chains", 31990, "1992", "Grunge", "/imagenes/AliceInChains-Dirt.jpg",
				"Obra maestra del grunge y metal alternativo.", vendedor, null);
		crearSiFalta("white-pony", "White Pony", "Deftones", 32990, "2000", "Metal", "/imagenes/Deftones-WhitePony.jpg",
				"Álbum definitivo de Deftones en los 2000.", vendedor, 10);
		crearSiFalta("meteora", "Meteora", "Linkin Park", 29990, "2003", "Rock", "/imagenes/LinkinPark-Meteora.jpg",
				"Segundo álbum de estudio. Incluye Numb y Faint.", vendedor, 20);

		blogSiFalta("aenima", "Reseña", "¿Por qué Ænima sigue siendo un clásico?",
				"La mezcla de tensión y atmósfera de Tool lo convierte en una pieza fundamental para cualquier colección.",
				"Lanzado en 1996, Ænima consolidó a TOOL como una de las propuestas más originales del rock progresivo y alternativo.",
				"/imagenes/Tool-AEnima.jpg", "/productos/aenima", "10 de Octubre, 2026");
		blogSiFalta("white-pony", "Recomendación", "White Pony: el disco que hay que tener",
				"Deftones cruzó metal y clima en los 2000. Si estás armando tu estantería, este vinilo no puede faltar.",
				"White Pony de Deftones (2000) marcó un antes y un después en el sonido del metal alternativo.",
				"/imagenes/Deftones-WhitePony.jpg", "/productos/white-pony", "02 de Octubre, 2026");
		blogSiFalta("cuidado-vinilos", "Consejos", "Guía definitiva: ¿Cómo limpiar tus vinilos sin dañarlos?",
				"Descubre los mejores métodos, líquidos recomendados y el uso correcto de cepillos antiestáticos.",
				"El vinilo es un formato delicado. Usa un cepillo de fibra de carbono antes de cada reproducción.",
				"/imagenes/limpieza.jpg", null, "25 de Septiembre, 2026");
		admin.getId();
	}

	private void crearSiFalta(String id, String nombre, String artista, int precio, String anio, String categoria,
			String imagen, String descripcion, Usuario vendedor, Integer porcentaje) {
		Producto producto = productoRepository.findById(id).orElseGet(() -> {
			Producto nuevo = new Producto();
			nuevo.setId(id);
			nuevo.setNombre(nombre);
			nuevo.setArtista(artista);
			nuevo.setPrecio(BigDecimal.valueOf(precio));
			nuevo.setAnio(anio);
			nuevo.setCategoria(categoria);
			nuevo.setImagen(imagen);
			nuevo.setDescripcion(descripcion);
			nuevo.setVendedor(vendedor);
			return productoRepository.save(nuevo);
		});
		if (porcentaje != null && ofertaRepository.findByProducto_Id(id).isEmpty()) {
			Oferta oferta = new Oferta();
			oferta.setProducto(producto);
			oferta.setPorcentaje(porcentaje);
			oferta.setPrecioOferta(descontar(producto.getPrecio(), porcentaje));
			oferta.setActiva(true);
			ofertaRepository.save(oferta);
		}
	}

	private void blogSiFalta(String id, String categoria, String titulo, String extracto, String contenido,
			String imagen, String enlace, String fecha) {
		if (blogRepository.existsById(id)) {
			return;
		}
		PublicacionBlog post = new PublicacionBlog();
		post.setId(id);
		post.setCategoria(categoria);
		post.setTitulo(titulo);
		post.setExtracto(extracto);
		post.setContenidoCompleto(contenido);
		post.setImagen(imagen);
		post.setEnlaceProducto(enlace);
		post.setFecha(fecha);
		blogRepository.save(post);
	}

	private void copiarProducto(Producto producto, ProductoRequest datos) {
		producto.setNombre(texto(datos.nombre, "Sin nombre"));
		producto.setArtista(texto(datos.artista, "Sin artista"));
		producto.setPrecio(datos.precio == null ? BigDecimal.ZERO : datos.precio);
		producto.setAnio(texto(datos.anio, "2024"));
		producto.setCategoria(texto(datos.categoria, "Rock"));
		producto.setImagen(texto(datos.imagen, "/imagenes/default.jpg"));
		producto.setDescripcion(texto(datos.descripcion, "Producto agregado desde la API."));
		if (datos.vendedorId != null) {
			producto.setVendedor(usuarioObligatorio(datos.vendedorId));
		}
	}

	private void guardarOferta(Producto producto, boolean activa, BigDecimal precioPedido) {
		Oferta oferta = ofertaRepository.findByProducto_Id(producto.getId()).orElse(null);
		if (!activa) {
			if (oferta != null) {
				oferta.setActiva(false);
				ofertaRepository.save(oferta);
			}
			return;
		}
		if (oferta == null) {
			oferta = new Oferta();
			oferta.setProducto(producto);
			oferta.setPorcentaje(15);
		}
		BigDecimal base = precioPedido == null ? producto.getPrecio() : precioPedido;
		oferta.setPrecioOferta(descontar(base, oferta.getPorcentaje() == null ? 15 : oferta.getPorcentaje()));
		oferta.setActiva(true);
		ofertaRepository.save(oferta);
	}

	private void guardarPerfil(Usuario usuario, String nombreTienda, String descripcion, String ciudad) {
		PerfilVendedor perfil = perfilRepository.findByUsuario_Id(usuario.getId()).orElseGet(PerfilVendedor::new);
		perfil.setUsuario(usuario);
		perfil.setNombreTienda(texto(nombreTienda, "Tienda de " + usuario.getNombre()));
		perfil.setDescripcion(texto(descripcion, "Perfil de vendedor en Vinilove."));
		perfil.setCiudad(texto(ciudad, "Santiago"));
		perfilRepository.save(perfil);
	}

	private Carrito carritoDe(Usuario usuario) {
		return carritoRepository.findByUsuario_Id(usuario.getId()).orElseGet(() -> {
			Carrito carrito = new Carrito();
			carrito.setUsuario(usuario);
			return carritoRepository.save(carrito);
		});
	}

	private List<CarritoItemResponse> itemsDe(Carrito carrito) {
		List<CarritoItemResponse> lista = new ArrayList<>();
		for (CarritoItem item : carrito.getItems()) {
			CarritoItemResponse fila = new CarritoItemResponse();
			fila.id = item.getProducto().getId();
			fila.nombre = item.getProducto().getNombre();
			fila.artista = item.getProducto().getArtista();
			fila.precio = precioCobrado(item.getProducto());
			fila.cantidad = item.getCantidad();
			fila.imagen = item.getProducto().getImagen();
			lista.add(fila);
		}
		return lista;
	}

	private ProductoResponse aProducto(Producto producto) {
		Oferta oferta = ofertaRepository.findByProducto_Id(producto.getId()).orElse(null);
		ProductoResponse respuesta = new ProductoResponse();
		respuesta.id = producto.getId();
		respuesta.nombre = producto.getNombre();
		respuesta.artista = producto.getArtista();
		respuesta.precio = producto.getPrecio();
		respuesta.anio = producto.getAnio();
		respuesta.categoria = producto.getCategoria();
		respuesta.imagen = producto.getImagen();
		respuesta.descripcion = producto.getDescripcion();
		respuesta.oferta = oferta != null && oferta.isActiva();
		respuesta.precioOferta = respuesta.oferta ? oferta.getPrecioOferta() : null;
		respuesta.porcentaje = respuesta.oferta ? oferta.getPorcentaje() : null;
		if (producto.getVendedor() != null) {
			respuesta.vendedorId = producto.getVendedor().getId();
			respuesta.tienda = perfilRepository.findByUsuario_Id(producto.getVendedor().getId())
					.map(PerfilVendedor::getNombreTienda)
					.orElse(null);
		}
		return respuesta;
	}

	private OfertaResponse aOferta(Oferta oferta) {
		OfertaResponse respuesta = new OfertaResponse();
		respuesta.id = oferta.getId();
		respuesta.productoId = oferta.getProducto().getId();
		respuesta.nombre = oferta.getProducto().getNombre();
		respuesta.precio = oferta.getProducto().getPrecio();
		respuesta.precioOferta = oferta.getPrecioOferta();
		respuesta.porcentaje = oferta.getPorcentaje();
		respuesta.activa = oferta.isActiva();
		return respuesta;
	}

	private UsuarioResponse aUsuario(Usuario usuario) {
		UsuarioResponse respuesta = new UsuarioResponse();
		respuesta.id = usuario.getId();
		respuesta.run = usuario.getRun();
		respuesta.correo = usuario.getCorreo();
		respuesta.nombre = usuario.getNombre();
		respuesta.apellidos = usuario.getApellidos();
		respuesta.rol = usuario.getRol();
		respuesta.nombreUsuario = usuario.getNombreUsuario();
		respuesta.fotoPerfil = usuario.getFotoPerfil();
		perfilRepository.findByUsuario_Id(usuario.getId()).ifPresent(perfil -> {
			respuesta.nombreTienda = perfil.getNombreTienda();
			respuesta.descripcionTienda = perfil.getDescripcion();
			respuesta.ciudad = perfil.getCiudad();
		});
		return respuesta;
	}

	private PedidoResponse aPedido(Pedido pedido) {
		PedidoResponse respuesta = new PedidoResponse();
		respuesta.id = pedido.getId();
		respuesta.usuarioId = pedido.getUsuario() == null ? null : pedido.getUsuario().getId();
		respuesta.nombre = pedido.getNombreEntrega();
		respuesta.direccion = pedido.getDireccion();
		respuesta.total = pedido.getTotal();
		respuesta.estado = pedido.getEstado();
		respuesta.creadoEn = pedido.getCreadoEn();
		for (PedidoItem item : pedido.getItems()) {
			PedidoItemResponse fila = new PedidoItemResponse();
			fila.productoId = item.getProductoId();
			fila.nombre = item.getNombre();
			fila.precio = item.getPrecio();
			fila.cantidad = item.getCantidad();
			respuesta.items.add(fila);
		}
		return respuesta;
	}

	private MensajeResponse aMensaje(MensajeContacto mensaje) {
		MensajeResponse respuesta = new MensajeResponse();
		respuesta.id = mensaje.getId();
		respuesta.nombre = mensaje.getNombre();
		respuesta.correo = mensaje.getCorreo();
		respuesta.comentario = mensaje.getComentario();
		respuesta.creadoEn = mensaje.getCreadoEn();
		return respuesta;
	}

	private BlogResponse aBlog(PublicacionBlog post) {
		BlogResponse respuesta = new BlogResponse();
		respuesta.id = post.getId();
		respuesta.categoria = post.getCategoria();
		respuesta.titulo = post.getTitulo();
		respuesta.extracto = post.getExtracto();
		respuesta.contenidoCompleto = post.getContenidoCompleto();
		respuesta.imagen = post.getImagen();
		respuesta.enlaceProducto = post.getEnlaceProducto();
		respuesta.fecha = post.getFecha();
		return respuesta;
	}

	private BigDecimal precioCobrado(Producto producto) {
		return ofertaRepository.findByProducto_Id(producto.getId())
				.filter(Oferta::isActiva)
				.map(Oferta::getPrecioOferta)
				.orElse(producto.getPrecio());
	}

	private BigDecimal descontar(BigDecimal precio, int porcentaje) {
		BigDecimal base = precio == null ? BigDecimal.ZERO : precio;
		return base.multiply(BigDecimal.valueOf(100 - porcentaje))
				.divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP);
	}

	private Producto productoObligatorio(String id) {
		return productoRepository.findById(id == null ? "" : id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado."));
	}

	private Usuario usuarioObligatorio(Long id) {
		return usuarioRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado."));
	}

	private String idDeItem(ItemRequest item) {
		if (item.productoId != null && !item.productoId.isBlank()) {
			return item.productoId;
		}
		return item.id;
	}

	private String texto(String valor, String porDefecto) {
		return valor == null || valor.isBlank() ? porDefecto : valor;
	}

	private String nombreUsuarioLibre(String pedido, String nombre) {
		String base = pedido == null || pedido.isBlank() ? slug(nombre) : pedido.trim();
		if (usuarioRepository.existsByNombreUsuarioIgnoreCase(base)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Ese nombre de usuario ya está ocupado.");
		}
		return base;
	}

	private String slug(String texto) {
		String base = texto == null ? "producto" : texto;
		String normal = Normalizer.normalize(base, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
		String limpio = normal.toLowerCase().trim().replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
		return limpio.isBlank() ? "producto" : limpio;
	}
}
