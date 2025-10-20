package com.TaleScoper.TaleScoper.user.service.serviceImpl;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.TaleScoper.TaleScoper.user.service.UsersService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsersServiceImpl implements UsersService {

	@Override
	public ResponseEntity<?> verify() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'verify'");
	}

	@Override
	public String generateRandomUsername(String name) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'generateRandomUsername'");
	}

	@Override
	public boolean checkUniqueUsername(String username) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'checkUniqueUsername'");
	}

}
