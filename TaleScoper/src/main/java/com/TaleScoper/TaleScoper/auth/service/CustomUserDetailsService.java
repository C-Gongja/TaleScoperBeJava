package com.TaleScoper.TaleScoper.auth.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.TaleScoper.TaleScoper.auth.customUserDetails.CustomUserDetails;
import com.TaleScoper.TaleScoper.user.entity.Users;
import com.TaleScoper.TaleScoper.user.repository.UsersRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {
	@Autowired
	private UsersRepository usersRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Optional<Users> userDetail = usersRepository.findByEmail(email);

		return userDetail.map(user -> CustomUserDetails.from(user))
				.orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
	}
}
