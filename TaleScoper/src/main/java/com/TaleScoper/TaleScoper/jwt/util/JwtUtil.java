package com.TaleScoper.TaleScoper.jwt.util;

import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.stereotype.Component;

import com.TaleScoper.TaleScoper.auth.customUserDetails.CustomUserDetails;

@Component
public class JwtUtil {

	@Value("${jwt.secret}")
	private String secretKey;

	@Value("${jwt.access-token-expiration:3600}")
	private long accessTokenExpiration;

	@Value("${jwt.refresh-token-expiration:604800}")
	private long refreshTokenExpiration;

	// JWT Claims 생성 (인코딩은 서비스에서)
	public JwtClaimsSet createAccessTokenClaims(CustomUserDetails userDetails) {
		Instant now = Instant.now();
		Instant expiry = now.plusSeconds(accessTokenExpiration);

		return JwtClaimsSet.builder()
				.issuer("your-app")
				.subject(userDetails.getUsername())
				.audience(List.of("your-app-client"))
				.issuedAt(now)
				.expiresAt(expiry)
				.claim("userId", userDetails.getUid())
				.claim("roles", userDetails.getAuthorities().stream()
						.map(GrantedAuthority::getAuthority)
						.toList())
				.claim("tokenType", "access")
				.build();
	}

	public JwtClaimsSet createRefreshTokenClaims(CustomUserDetails userPrincipal) {
		Instant now = Instant.now();
		Instant expiry = now.plusSeconds(refreshTokenExpiration);

		return JwtClaimsSet.builder()
				.issuer("your-app")
				.subject(userPrincipal.getUsername())
				.issuedAt(now)
				.expiresAt(expiry)
				.claim("userId", userPrincipal.getUid())
				.claim("tokenType", "refresh")
				.build();
	}

	// 검증 메서드들
	public boolean isTokenExpired(Jwt jwt) {
		return jwt.getExpiresAt().isBefore(Instant.now());
	}

	public boolean isAccessToken(Jwt jwt) {
		return "access".equals(jwt.getClaimAsString("tokenType"));
	}

	public boolean isRefreshToken(Jwt jwt) {
		return "refresh".equals(jwt.getClaimAsString("tokenType"));
	}

	// UID 추출 메서드
	public String getUserUidFromJwt(Jwt jwt) {
		return jwt.getClaimAsString("userUid");
	}

	public Long getUserIdFromJwt(Jwt jwt) {
		return jwt.getClaim("userId");
	}

	@SuppressWarnings("unchecked")
	public List<String> getRolesFromJwt(Jwt jwt) {
		return jwt.getClaim("roles");
	}

	// Getter methods
	public String getSecretKey() {
		return secretKey;
	}

	public long getAccessTokenExpiration() {
		return accessTokenExpiration;
	}

	public long getRefreshTokenExpiration() {
		return refreshTokenExpiration;
	}
}
