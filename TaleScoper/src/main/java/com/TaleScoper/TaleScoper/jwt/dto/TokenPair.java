package com.TaleScoper.TaleScoper.jwt.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenPair {
	private String accessToken;
	private String refreshToken;
	private String tokenType;
	private long expiresIn;
}