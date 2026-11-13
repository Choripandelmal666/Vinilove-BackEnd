-- Bloque 3: base de datos de Vinilove (MySQL).
-- Ejecutar en MySQL Workbench o en la consola, con el mismo usuario del trabajo anterior.
-- Spring Boot también puede crear las tablas al partir, pero este script es la definición aparte.
--
-- La figura 1 del anexo nombra Usuario, Boleta, Detalle boleta, Producto y Categoría.
-- Acá eso es: usuarios, pedidos, pedido_items, productos y categorias.
-- Carrito, ofertas, perfil de vendedor, contacto y blog son tablas extra de la tienda.
-- Se pueden agregar columnas o tablas nuevas sin borrar las que ya usa React.

CREATE DATABASE IF NOT EXISTS vinilove
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_spanish_ci;

USE vinilove;

CREATE TABLE IF NOT EXISTS usuarios (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  run VARCHAR(12) NOT NULL UNIQUE,
  correo VARCHAR(100) NOT NULL UNIQUE,
  nombre VARCHAR(100) NOT NULL,
  apellidos VARCHAR(100),
  password VARCHAR(20) NOT NULL,
  rol VARCHAR(20) NOT NULL,
  nombre_usuario VARCHAR(40) NULL UNIQUE,
  foto_perfil LONGTEXT NULL
);

CREATE TABLE IF NOT EXISTS perfiles_vendedor (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  usuario_id BIGINT NOT NULL UNIQUE,
  nombre_tienda VARCHAR(150) NOT NULL,
  descripcion TEXT,
  ciudad VARCHAR(80),
  CONSTRAINT fk_perfil_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

CREATE TABLE IF NOT EXISTS productos (
  id VARCHAR(120) PRIMARY KEY,
  nombre VARCHAR(150) NOT NULL,
  artista VARCHAR(150) NOT NULL,
  precio DECIMAL(10,2) NOT NULL DEFAULT 0,
  anio VARCHAR(10) DEFAULT '2024',
  categoria VARCHAR(60) NOT NULL DEFAULT 'Rock',
  imagen LONGTEXT NOT NULL,
  descripcion TEXT,
  vendedor_id BIGINT NULL,
  CONSTRAINT fk_producto_vendedor FOREIGN KEY (vendedor_id) REFERENCES usuarios(id)
);

CREATE TABLE IF NOT EXISTS ofertas (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  producto_id VARCHAR(120) NOT NULL UNIQUE,
  precio_oferta DECIMAL(10,2) NOT NULL,
  porcentaje INT NOT NULL,
  activa TINYINT(1) NOT NULL DEFAULT 1,
  CONSTRAINT fk_oferta_producto FOREIGN KEY (producto_id) REFERENCES productos(id)
);

CREATE TABLE IF NOT EXISTS carritos (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  usuario_id BIGINT NOT NULL UNIQUE,
  CONSTRAINT fk_carrito_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

CREATE TABLE IF NOT EXISTS carrito_items (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  carrito_id BIGINT NOT NULL,
  producto_id VARCHAR(120) NOT NULL,
  cantidad INT NOT NULL,
  UNIQUE (carrito_id, producto_id),
  CONSTRAINT fk_item_carrito FOREIGN KEY (carrito_id) REFERENCES carritos(id),
  CONSTRAINT fk_item_producto FOREIGN KEY (producto_id) REFERENCES productos(id)
);

CREATE TABLE IF NOT EXISTS pedidos (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  usuario_id BIGINT NULL,
  nombre_entrega VARCHAR(150) NOT NULL,
  direccion VARCHAR(255) NOT NULL,
  total DECIMAL(10,2) NOT NULL,
  estado VARCHAR(30) NOT NULL,
  creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_pedido_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

CREATE TABLE IF NOT EXISTS pedido_items (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  pedido_id BIGINT NOT NULL,
  producto_id VARCHAR(120),
  nombre VARCHAR(150) NOT NULL,
  precio DECIMAL(10,2) NOT NULL,
  cantidad INT NOT NULL,
  CONSTRAINT fk_pedido_item FOREIGN KEY (pedido_id) REFERENCES pedidos(id)
);

CREATE TABLE IF NOT EXISTS mensajes_contacto (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  nombre VARCHAR(100) NOT NULL,
  correo VARCHAR(100),
  comentario VARCHAR(500) NOT NULL,
  creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS publicaciones_blog (
  id VARCHAR(120) PRIMARY KEY,
  categoria VARCHAR(60) NOT NULL,
  titulo VARCHAR(200) NOT NULL,
  extracto TEXT,
  contenido_completo TEXT,
  imagen LONGTEXT,
  enlace_producto VARCHAR(255),
  fecha VARCHAR(80)
);

INSERT INTO usuarios (run, correo, nombre, apellidos, password, rol, nombre_usuario)
SELECT '19011022K', 'admin@vinilove.com', 'Admin', 'Vinilove', 'admin1234', 'admin', 'admin'
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE correo = 'admin@vinilove.com');

INSERT INTO usuarios (run, correo, nombre, apellidos, password, rol, nombre_usuario)
SELECT '20111222K', 'vendedor@vinilove.com', 'Camila', 'Soto', 'vende1234', 'vendedor', 'camila'
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE correo = 'vendedor@vinilove.com');

INSERT INTO perfiles_vendedor (usuario_id, nombre_tienda, descripcion, ciudad)
SELECT u.id, 'Surcos Santiago', 'Vinilos de rock, metal y grunge elegidos a mano.', 'Santiago'
FROM usuarios u
WHERE u.correo = 'vendedor@vinilove.com'
  AND NOT EXISTS (SELECT 1 FROM perfiles_vendedor p WHERE p.usuario_id = u.id);

INSERT INTO productos (id, nombre, artista, precio, anio, categoria, imagen, descripcion, vendedor_id)
SELECT 'aenima', 'Ænima', 'TOOL', 34990, '1996', 'Metal', '/imagenes/Tool-AEnima.jpg',
       'Segundo álbum de estudio de TOOL. Clásico en vinilo 180g.', u.id
FROM usuarios u
WHERE u.correo = 'vendedor@vinilove.com'
  AND NOT EXISTS (SELECT 1 FROM productos WHERE id = 'aenima');

INSERT INTO productos (id, nombre, artista, precio, anio, categoria, imagen, descripcion, vendedor_id)
SELECT 'jar-of-flies', 'Jar of Flies', 'Alice in Chains', 28990, '1994', 'Grunge', '/imagenes/AliceInChains-JarOfflies.jpg',
       'EP icónico. Primer EP en llegar al número 1 del Billboard 200.', u.id
FROM usuarios u
WHERE u.correo = 'vendedor@vinilove.com'
  AND NOT EXISTS (SELECT 1 FROM productos WHERE id = 'jar-of-flies');

INSERT INTO productos (id, nombre, artista, precio, anio, categoria, imagen, descripcion, vendedor_id)
SELECT 'dirt', 'Dirt', 'Alice in Chains', 31990, '1992', 'Grunge', '/imagenes/AliceInChains-Dirt.jpg',
       'Obra maestra del grunge y metal alternativo.', u.id
FROM usuarios u
WHERE u.correo = 'vendedor@vinilove.com'
  AND NOT EXISTS (SELECT 1 FROM productos WHERE id = 'dirt');

INSERT INTO productos (id, nombre, artista, precio, anio, categoria, imagen, descripcion, vendedor_id)
SELECT 'white-pony', 'White Pony', 'Deftones', 32990, '2000', 'Metal', '/imagenes/Deftones-WhitePony.jpg',
       'Álbum definitivo de Deftones en los 2000.', u.id
FROM usuarios u
WHERE u.correo = 'vendedor@vinilove.com'
  AND NOT EXISTS (SELECT 1 FROM productos WHERE id = 'white-pony');

INSERT INTO productos (id, nombre, artista, precio, anio, categoria, imagen, descripcion, vendedor_id)
SELECT 'meteora', 'Meteora', 'Linkin Park', 29990, '2003', 'Rock', '/imagenes/LinkinPark-Meteora.jpg',
       'Segundo álbum de estudio. Incluye Numb y Faint.', u.id
FROM usuarios u
WHERE u.correo = 'vendedor@vinilove.com'
  AND NOT EXISTS (SELECT 1 FROM productos WHERE id = 'meteora');

INSERT INTO ofertas (producto_id, precio_oferta, porcentaje, activa)
SELECT 'jar-of-flies', 24642, 15, 1
WHERE NOT EXISTS (SELECT 1 FROM ofertas WHERE producto_id = 'jar-of-flies');

INSERT INTO ofertas (producto_id, precio_oferta, porcentaje, activa)
SELECT 'white-pony', 29691, 10, 1
WHERE NOT EXISTS (SELECT 1 FROM ofertas WHERE producto_id = 'white-pony');

INSERT INTO ofertas (producto_id, precio_oferta, porcentaje, activa)
SELECT 'meteora', 23992, 20, 1
WHERE NOT EXISTS (SELECT 1 FROM ofertas WHERE producto_id = 'meteora');

INSERT INTO publicaciones_blog (id, categoria, titulo, extracto, contenido_completo, imagen, enlace_producto, fecha)
SELECT 'aenima', 'Reseña', '¿Por qué Ænima sigue siendo un clásico?',
       'La mezcla de tensión y atmósfera de Tool lo convierte en una pieza fundamental para cualquier colección.',
       'Lanzado en 1996, Ænima consolidó a TOOL como una de las propuestas más originales del rock progresivo y alternativo.',
       '/imagenes/Tool-AEnima.jpg', '/productos/aenima', '10 de Octubre, 2026'
WHERE NOT EXISTS (SELECT 1 FROM publicaciones_blog WHERE id = 'aenima');

INSERT INTO publicaciones_blog (id, categoria, titulo, extracto, contenido_completo, imagen, enlace_producto, fecha)
SELECT 'white-pony', 'Recomendación', 'White Pony: el disco que hay que tener',
       'Deftones cruzó metal y clima en los 2000. Si estás armando tu estantería, este vinilo no puede faltar.',
       'White Pony de Deftones (2000) marcó un antes y un después en el sonido del metal alternativo.',
       '/imagenes/Deftones-WhitePony.jpg', '/productos/white-pony', '02 de Octubre, 2026'
WHERE NOT EXISTS (SELECT 1 FROM publicaciones_blog WHERE id = 'white-pony');

INSERT INTO publicaciones_blog (id, categoria, titulo, extracto, contenido_completo, imagen, enlace_producto, fecha)
SELECT 'cuidado-vinilos', 'Consejos', 'Guía definitiva: ¿Cómo limpiar tus vinilos sin dañarlos?',
       'Descubre los mejores métodos, líquidos recomendados y el uso correcto de cepillos antiestáticos.',
       'El vinilo es un formato delicado. Usa un cepillo de fibra de carbono antes de cada reproducción.',
       '/imagenes/limpieza.jpg', NULL, '25 de Septiembre, 2026'
WHERE NOT EXISTS (SELECT 1 FROM publicaciones_blog WHERE id = 'cuidado-vinilos');

CREATE TABLE IF NOT EXISTS categorias (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  nombre VARCHAR(60) NOT NULL UNIQUE
);

INSERT INTO categorias (nombre)
SELECT 'Rock' WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE nombre = 'Rock');

INSERT INTO categorias (nombre)
SELECT 'Metal' WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE nombre = 'Metal');

INSERT INTO categorias (nombre)
SELECT 'Grunge' WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE nombre = 'Grunge');

-- El login de hoy guarda la contraseña corta. Este cambio deja espacio para el hash
-- cuando Spring pase a JWT, sin tocar las cuentas que ya existen.
SET @largo_password = (
  SELECT CHARACTER_MAXIMUM_LENGTH
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'usuarios'
    AND COLUMN_NAME = 'password'
);
SET @sql_password = IF(
  @largo_password IS NULL OR @largo_password >= 255,
  'SELECT 1',
  'ALTER TABLE usuarios MODIFY password VARCHAR(255) NOT NULL'
);
PREPARE stmt_password FROM @sql_password;
EXECUTE stmt_password;
DEALLOCATE PREPARE stmt_password;
