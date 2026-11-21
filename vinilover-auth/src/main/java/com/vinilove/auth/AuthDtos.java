package com.vinilove.auth;

public final class AuthDtos {

	private AuthDtos() {}

	public record RegistroRequest(
			String run,
			String correo,
			String nombre,
			String apellidos,
			String password,
			String rol,
			String nombreUsuario,
			String nombreTienda,
			String descripcion,
			String ciudad) {}

	public record LoginRequest(String correo, String password) {}

	public record GoogleRequest(String credential) {}

	public record PerfilRequest(String nombreUsuario, String fotoPerfil) {}

	public record VendedorRequest(String nombreTienda, String descripcion, String ciudad) {}

	public record UsuarioResponse(
			Long id,
			String token,
			String run,
			String correo,
			String nombre,
			String apellidos,
			String rol,
			String nombreUsuario,
			String fotoPerfil,
			String nombreTienda,
			String descripcionTienda,
			String ciudad) {}
}
