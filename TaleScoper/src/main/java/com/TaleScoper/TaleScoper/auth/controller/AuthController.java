package com.TaleScoper.TaleScoper.auth.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.TaleScoper.TaleScoper.auth.customUserDetails.CustomUserDetails;
import com.TaleScoper.TaleScoper.auth.dto.RegisterRequest;
import com.TaleScoper.TaleScoper.auth.dto.SigninRequest;
import com.TaleScoper.TaleScoper.auth.service.AuthService;
import com.TaleScoper.TaleScoper.jwt.dto.AccessTokenResponse;
import com.TaleScoper.TaleScoper.jwt.dto.TokenPair;
import com.TaleScoper.TaleScoper.jwt.service.JwtService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@Validated
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;
	private final JwtService jwtService;

	@PostMapping("/login")
	public ResponseEntity<AccessTokenResponse> login(@Valid @RequestBody SigninRequest request) {
		CustomUserDetails customUserDetails = authService.authenticate(request);
		TokenPair tokens = jwtService.generateTokens(customUserDetails);
		// Refresh Token은 쿠키에 설정
		ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", tokens.getRefreshToken())
				.httpOnly(true) // JS에서 접근 불가 (XSS 방지)
				.secure(false) // HTTPS 환경에서 true (개발환경에서 http면 false)
				.sameSite("Strict") // 크로스사이트 요청 시 전달 금지
				.path("/") // 쿠키 적용 범위
				.maxAge(7 * 24 * 60 * 60) // refreshToken 유효기간 (예: 7일)
				.build();

		// Access Token만 JSON으로 반환
		AccessTokenResponse responseBody = AccessTokenResponse.builder()
				.accessToken(tokens.getAccessToken())
				.build();

		return ResponseEntity
				.status(HttpStatus.OK)
				.header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
				.body(responseBody);
	}

	@PostMapping("/register")
	public ResponseEntity<AccessTokenResponse> register(@Valid @RequestBody RegisterRequest request) {
		CustomUserDetails customUserDetails = authService.register(request);
		TokenPair tokens = jwtService.generateTokens(customUserDetails);
		// Refresh Token은 쿠키에 설정
		ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", tokens.getRefreshToken())
				.httpOnly(true) // JS에서 접근 불가 (XSS 방지)
				.secure(false) // HTTPS 환경에서 true (개발환경에서 http면 false)
				.sameSite("Strict") // 크로스사이트 요청 시 전달 금지
				.path("/") // 쿠키 적용 범위
				.maxAge(7 * 24 * 60 * 60) // refreshToken 유효기간 (예: 7일)
				.build();

		// Access Token만 JSON으로 반환
		AccessTokenResponse responseBody = AccessTokenResponse.builder()
				.accessToken(tokens.getAccessToken())
				.build();

		return ResponseEntity
				.status(HttpStatus.OK)
				.header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
				.body(responseBody);
	}

	@PostMapping("/refresh")
	public ResponseEntity<AccessTokenResponse> refresh(@CookieValue("refreshToken") String refreshToken) {
		// refreshToken 유효성 검증
		if (!jwtService.validateRefreshToken(refreshToken)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		// 새로운 accessToken 발급
		Jwt jwt = jwtService.parseToken(refreshToken);
		String username = jwt.getSubject();
		CustomUserDetails user = authService.loadUserByUsername(username);

		TokenPair newTokens = jwtService.generateTokens(user);

		// refreshToken 재발급 시 쿠키 업데이트
		ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", newTokens.getRefreshToken())
				.httpOnly(true)
				.secure(false) // HTTPS 환경에서 true (개발환경에서 http면 false)
				.sameSite("Strict")
				.path("/")
				.maxAge(7 * 24 * 60 * 60)
				.build();

		AccessTokenResponse body = AccessTokenResponse.builder()
				.accessToken(newTokens.getAccessToken())
				.build();

		return ResponseEntity.ok()
				.header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
				.body(body);
	}

	@PostMapping("/logout")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<Void> logout(Authentication authentication) {
		authService.logout(authentication.getName());
		return ResponseEntity.ok().build();
	}
}
