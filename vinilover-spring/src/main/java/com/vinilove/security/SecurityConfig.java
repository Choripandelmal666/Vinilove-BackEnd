package com.vinilove.security;

import java.io.IOException;
import java.security.interfaces.RSAPublicKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.converter.RsaKeyConverters;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableMethodSecurity
public class SecurityConfig {

	@Bean
	JwtDecoder jwtDecoder(@Value("${vinilove.jwt.public-key}") Resource publicKeyResource) throws IOException {
		RSAPublicKey publicKey = RsaKeyConverters.x509().convert(publicKeyResource.getInputStream());
		return NimbusJwtDecoder.withPublicKey(publicKey).signatureAlgorithm(SignatureAlgorithm.RS256).build();
	}

	@Bean
	JwtAuthenticationConverter jwtAuthenticationConverter() {
		JwtGrantedAuthoritiesConverter authorities = new JwtGrantedAuthoritiesConverter();
		authorities.setAuthoritiesClaimName("role");
		authorities.setAuthorityPrefix("ROLE_");
		JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
		converter.setJwtGrantedAuthoritiesConverter(authorities);
		return converter;
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationConverter jwtConverter) throws Exception {
		return http
				.csrf(csrf -> csrf.disable())
				.cors(Customizer.withDefaults())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
						.requestMatchers(HttpMethod.POST, "/api/usuarios/registro", "/api/usuarios/login").permitAll()
						.requestMatchers("/api/health", "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/productos/**", "/api/ofertas/**", "/api/blog/**").permitAll()
						.requestMatchers(HttpMethod.POST, "/api/contacto").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/contacto", "/api/usuarios").hasRole("ADMIN")
						.requestMatchers(HttpMethod.GET, "/api/pedidos").hasAnyRole("ADMIN", "VENDEDOR")
						.requestMatchers(HttpMethod.POST, "/api/productos/**", "/api/ofertas/**")
								.hasAnyRole("ADMIN", "VENDEDOR")
						.requestMatchers(HttpMethod.PUT, "/api/productos/**").hasAnyRole("ADMIN", "VENDEDOR")
						.requestMatchers(HttpMethod.DELETE, "/api/productos/**", "/api/ofertas/**")
								.hasAnyRole("ADMIN", "VENDEDOR")
						.requestMatchers(HttpMethod.POST, "/api/blog/**").hasRole("ADMIN")
						.requestMatchers(HttpMethod.DELETE, "/api/blog/**").hasRole("ADMIN")
						.anyRequest().authenticated())
				.oauth2ResourceServer(resource -> resource.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtConverter)))
				.build();
	}
}