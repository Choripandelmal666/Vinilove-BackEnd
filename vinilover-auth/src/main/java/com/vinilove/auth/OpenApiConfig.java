package com.vinilove.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

	@Bean
	OpenAPI usuariosApi() {
		return new OpenAPI().info(new Info()
				.title("Vinilove Usuarios")
				.version("1.0")
				.description("Registro, login, Google y perfiles."));
	}
}
