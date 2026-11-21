package com.vinilove.catalog;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

	@Bean
	OpenAPI catalogoApi() {
		return new OpenAPI().info(new Info()
				.title("Vinilove Catalogo")
				.version("1.0")
				.description("Productos y ofertas."));
	}
}
