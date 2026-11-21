package com.vinilove.auth.security;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;

import com.vinilove.auth.model.Usuario;

@Service
public class JwtTokenService {

	private final JwtEncoder encoder;

	public JwtTokenService(JwtEncoder encoder) {
		this.encoder = encoder;
	}

	public String emitir(Usuario usuario) {
		Instant ahora = Instant.now();
		JwtClaimsSet claims = JwtClaimsSet.builder()
				.issuer("vinilove")
				.issuedAt(ahora)
				.expiresAt(ahora.plus(12, ChronoUnit.HOURS))
				.subject(usuario.id.toString())
				.claim("email", usuario.correo)
				.claim("role", usuario.rol.toUpperCase(Locale.ROOT))
				.build();
		return encoder.encode(JwtEncoderParameters.from(
				JwsHeader.with(SignatureAlgorithm.RS256).build(), claims)).getTokenValue();
	}
}
