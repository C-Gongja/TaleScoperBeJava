package com.TaleScoper.TaleScoper.user.service;

import org.springframework.http.ResponseEntity;

public interface UsersService {
	public ResponseEntity<?> verify();

	public String generateRandomUsername(String name);

	public boolean checkUniqueUsername(String username);
}
