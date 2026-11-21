CREATE DATABASE IF NOT EXISTS vinilove_auth CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci;
CREATE DATABASE IF NOT EXISTS vinilove_catalog CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci;
CREATE DATABASE IF NOT EXISTS vinilove_cart CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci;

CREATE TABLE IF NOT EXISTS vinilove_auth.usuarios (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  run VARCHAR(12) NOT NULL UNIQUE,
  correo VARCHAR(100) NOT NULL UNIQUE,
  nombre VARCHAR(100) NOT NULL,
  apellidos VARCHAR(100),
  password VARCHAR(100) NOT NULL,
  rol VARCHAR(20) NOT NULL,
  nombre_usuario VARCHAR(40) NULL UNIQUE,
  foto_perfil LONGTEXT NULL,
  nombre_tienda VARCHAR(150) NULL,
  descripcion_tienda TEXT NULL,
  ciudad VARCHAR(80) NULL
);

INSERT INTO vinilove_auth.usuarios
  (id, run, correo, nombre, apellidos, password, rol, nombre_usuario, foto_perfil,
   nombre_tienda, descripcion_tienda, ciudad)
SELECT u.id, u.run, u.correo, u.nombre, u.apellidos, u.password, u.rol, u.nombre_usuario,
       u.foto_perfil, p.nombre_tienda, p.descripcion, p.ciudad
FROM vinilove.usuarios u
LEFT JOIN vinilove.perfiles_vendedor p ON p.usuario_id = u.id
WHERE NOT EXISTS (
  SELECT 1 FROM vinilove_auth.usuarios target WHERE target.id = u.id OR target.correo = u.correo
);

CREATE TABLE IF NOT EXISTS vinilove_catalog.productos (
  id VARCHAR(120) PRIMARY KEY,
  nombre VARCHAR(150) NOT NULL,
  artista VARCHAR(150) NOT NULL,
  precio DECIMAL(10,2) NOT NULL DEFAULT 0,
  anio VARCHAR(10) DEFAULT '2024',
  categoria VARCHAR(60) NOT NULL DEFAULT 'Rock',
  imagen LONGTEXT NOT NULL,
  descripcion TEXT,
  vendedor_id BIGINT NULL
);

INSERT INTO vinilove_catalog.productos
  (id, nombre, artista, precio, anio, categoria, imagen, descripcion, vendedor_id)
SELECT p.id, p.nombre, p.artista, p.precio, p.anio, p.categoria, p.imagen, p.descripcion, p.vendedor_id
FROM vinilove.productos p
WHERE NOT EXISTS (
  SELECT 1 FROM vinilove_catalog.productos target WHERE target.id = p.id
);

CREATE TABLE IF NOT EXISTS vinilove_catalog.ofertas (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  producto_id VARCHAR(120) NOT NULL UNIQUE,
  precio_oferta DECIMAL(10,2) NOT NULL,
  porcentaje INT NOT NULL,
  activa TINYINT(1) NOT NULL DEFAULT 1,
  CONSTRAINT fk_catalog_offer_product FOREIGN KEY (producto_id)
    REFERENCES vinilove_catalog.productos(id)
);

INSERT INTO vinilove_catalog.ofertas (id, producto_id, precio_oferta, porcentaje, activa)
SELECT o.id, o.producto_id, o.precio_oferta, o.porcentaje, o.activa
FROM vinilove.ofertas o
WHERE NOT EXISTS (
  SELECT 1 FROM vinilove_catalog.ofertas target WHERE target.producto_id = o.producto_id
);

CREATE TABLE IF NOT EXISTS vinilove_cart.carrito_items (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  usuario_id BIGINT NOT NULL,
  producto_id VARCHAR(120) NOT NULL,
  cantidad INT NOT NULL,
  UNIQUE KEY uk_cart_user_product (usuario_id, producto_id)
);

INSERT INTO vinilove_cart.carrito_items (usuario_id, producto_id, cantidad)
SELECT c.usuario_id, ci.producto_id, ci.cantidad
FROM vinilove.carritos c
JOIN vinilove.carrito_items ci ON ci.carrito_id = c.id
WHERE NOT EXISTS (
  SELECT 1 FROM vinilove_cart.carrito_items target
  WHERE target.usuario_id = c.usuario_id AND target.producto_id = ci.producto_id
);
