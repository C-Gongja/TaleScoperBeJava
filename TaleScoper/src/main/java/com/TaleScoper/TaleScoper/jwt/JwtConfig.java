package com.TaleScoper.TaleScoper.jwt;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import com.nimbusds.jose.jwk.source.ImmutableSecret;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class JwtConfig {

	@Value("${jwt.secret}")
	private String secretKey;

	@PostConstruct
	public void validateSecretKey() {
		log.info("=".repeat(80));
		log.info("Validating JWT Secret Key...");

		if (secretKey == null || secretKey.trim().isEmpty()) {
			throw new IllegalStateException(
					"JWT secret key is not configured. Please set 'jwt.secret' in application.properties");
		}

		int keyLength = secretKey.getBytes().length;
		log.info("JWT secret key length: {} bytes", keyLength);

		if (keyLength < 32) {
			throw new IllegalStateException(
					String.format(
							"JWT secret key must be at least 32 bytes (256 bits) long for HS256. " +
									"Current length: %d bytes. Please use a longer secret key.",
							keyLength));
		}

		log.info("JWT secret key validated successfully!");
		log.info("=".repeat(80));
	}

	@Bean
	public SecretKey jwtSecretKey() {
		// log.info("Creating SecretKey bean...");
		SecretKey key = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
		// log.info("SecretKey bean created successfully");
		return key;
	}

	@Bean
	public JwtDecoder jwtDecoder(SecretKey jwtSecretKey) {
		// log.info("Creating JwtDecoder bean...");
		JwtDecoder decoder = NimbusJwtDecoder.withSecretKey(jwtSecretKey)
				.macAlgorithm(MacAlgorithm.HS256)
				.build();
		// log.info("JwtDecoder bean created successfully");
		return decoder;
	}

	@Bean
	public JwtEncoder jwtEncoder(SecretKey jwtSecretKey) {
		log.info("Creating JwtEncoder bean...");
		try {
			NimbusJwtEncoder encoder = new NimbusJwtEncoder(new ImmutableSecret<>(jwtSecretKey));
			log.info("JwtEncoder bean created successfully");
			return encoder;
		} catch (Exception e) {
			log.error("Failed to create JwtEncoder: {}", e.getMessage(), e);
			throw e;
		}
	}

	@Bean
	public JwtAuthenticationConverter jwtAuthenticationConverter() {
		log.info("Creating JwtAuthenticationConverter bean...");

		JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
		authoritiesConverter.setAuthorityPrefix("ROLE_");
		authoritiesConverter.setAuthoritiesClaimName("roles");

		JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
		jwtConverter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
		jwtConverter.setPrincipalClaimName("sub");

		log.info("JwtAuthenticationConverter bean created successfully");
		return jwtConverter;
	}
}