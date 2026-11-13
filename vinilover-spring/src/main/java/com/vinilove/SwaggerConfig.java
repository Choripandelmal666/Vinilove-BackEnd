package com.vinilove;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI api() {
		return new OpenAPI()
				.info(new Info()
						.title("Vinilove API")
						.version("1.0")
						.description("React, Spring Boot y MySQL. Productos, ofertas, carrito, pedidos, usuarios, vendedores, contacto y blog."));
	}
}
