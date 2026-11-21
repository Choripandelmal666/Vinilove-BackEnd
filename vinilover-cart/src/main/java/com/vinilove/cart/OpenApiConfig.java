package com.vinilove.cart;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

	@Bean
	OpenAPI carritoApi() {
		return new OpenAPI().info(new Info()
				.title("Vinilove Carrito")
				.version("1.0")
				.description("Carrito del cliente."));
	}
}
