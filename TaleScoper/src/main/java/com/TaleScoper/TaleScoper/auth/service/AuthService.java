package com.TaleScoper.TaleScoper.auth.service;

import com.TaleScoper.TaleScoper.auth.customUserDetails.CustomUserDetails;
import com.TaleScoper.TaleScoper.auth.dto.RegisterRequest;
import com.TaleScoper.TaleScoper.auth.dto.SigninRequest;

public interface AuthService {
	CustomUserDetails authenticate(SigninRequest request);

	CustomUserDetails loadUserByUserId(Long id);

	CustomUserDetails loadUserByUid(String uid);

	CustomUserDetails loadUserByEmail(String email);

	CustomUserDetails loadUserByUsername(String username);

	CustomUserDetails register(RegisterRequest request);

	boolean existsByEmail(String email);

	void logout(String username);
}
