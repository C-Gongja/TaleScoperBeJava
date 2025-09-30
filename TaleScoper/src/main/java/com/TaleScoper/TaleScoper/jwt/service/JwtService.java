package com.TaleScoper.TaleScoper.jwt.service;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.TaleScoper.TaleScoper.auth.customUserDetails.CustomUserDetails;
import com.TaleScoper.TaleScoper.jwt.dto.TokenPair;
import com.TaleScoper.TaleScoper.jwt.util.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JwtService {

	private final JwtUtil jwtUtil;
	private final JwtEncoder jwtEncoder;
	private final JwtDecoder jwtDecoder;

	public TokenPair generateTokens(CustomUserDetails userPrincipal) {
		String accessToken = createAccessToken(userPrincipal);
		String refreshToken = createRefreshToken(userPrincipal);

		return TokenPair.builder()
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.tokenType("Bearer")
				.expiresIn(jwtUtil.getAccessTokenExpiration())
				.build();
	}

	/*
	 * NimbusJwtEncoder는 내부적으로 JWKSource(여기서는 ImmutableSecret)에서 사용할 JWK를 선택할 때 JWS
	 * 알고리즘(alg) 정보를 기준으로 필터링합니다.
	 * 하지만 JwtEncoderParameters.from(claims)로만 전달하면 JWS 헤더(alg)가 명시적으로 포함되지 않으므로,
	 * Nimbus가 JWK를 올바르게 매칭하지 못해 "Failed to select a JWK signing key" 예외가 발생할 수
	 * 있습니다.
	 * 
	 * 즉: Claims만 전달해서는 어떤 알고리즘으로 서명할지 암묵적으로 모호하게 되고, JWK 선택이 실패할 수 있습니다.
	 * 그래서 jwsHeader로 HMAC 알고리즘을 명시해서 전달.
	 * 이렇게 하면 Nimbus가 명시된 alg = HS256을 기준으로 ImmutableSecret에서 생성한 oct key(JWK)를 올바르게
	 * 선택하여 서명합니다.
	 */

	private String createAccessToken(CustomUserDetails userPrincipal) {
		JwtClaimsSet claims = jwtUtil.createAccessTokenClaims(userPrincipal);
		JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS256).build();
		return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
	}

	private String createRefreshToken(CustomUserDetails userPrincipal) {
		JwtClaimsSet claims = jwtUtil.createRefreshTokenClaims(userPrincipal);
		JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS256).build();
		return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
	}

	// 토큰 검증 메서드들
	public boolean validateToken(String token) {
		try {
			Jwt jwt = jwtDecoder.decode(token);
			return !jwtUtil.isTokenExpired(jwt);
		} catch (JwtException e) {
			return false;
		}
	}

	public boolean validateAccessToken(String token) {
		try {
			Jwt jwt = jwtDecoder.decode(token);
			return !jwtUtil.isTokenExpired(jwt) && jwtUtil.isAccessToken(jwt);
		} catch (JwtException e) {
			return false;
		}
	}

	public boolean validateRefreshToken(String token) {
		try {
			Jwt jwt = jwtDecoder.decode(token);
			return !jwtUtil.isTokenExpired(jwt) && jwtUtil.isRefreshToken(jwt);
		} catch (JwtException e) {
			return false;
		}
	}

	public Jwt parseToken(String token) {
		return jwtDecoder.decode(token);
	}
}
