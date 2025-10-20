package com.TaleScoper.TaleScoper.auth.service.serviceImpl;

import java.util.Set;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.TaleScoper.TaleScoper.auth.customUserDetails.CustomUserDetails;
import com.TaleScoper.TaleScoper.auth.dto.RegisterRequest;
import com.TaleScoper.TaleScoper.auth.dto.SigninRequest;
import com.TaleScoper.TaleScoper.auth.service.AuthService;
import com.TaleScoper.TaleScoper.role.entity.Role;
import com.TaleScoper.TaleScoper.role.repository.RoleRepository;
import com.TaleScoper.TaleScoper.user.entity.Users;
import com.TaleScoper.TaleScoper.user.repository.UsersRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AuthServiceImpl implements AuthService {
	private final UsersRepository usersRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public CustomUserDetails authenticate(SigninRequest request) {
		log.debug("Authenticating user with email: {}", request.getEmail());

		Users user = usersRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + request.getEmail()));

		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			log.warn("Invalid password attempt for user: {}", request.getEmail());
			throw new BadCredentialsException("Invalid email or password");
		}

		if (!user.isEnabled()) {
			throw new BadCredentialsException("Account is disabled");
		}

		log.info("User authenticated successfully: {}", request.getEmail());
		return CustomUserDetails.from(user);
	}

	@Override
	public CustomUserDetails loadUserByUserId(Long id) {
		log.debug("Loading user by ID: {}", id);

		Users user = usersRepository.findById(id)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + id));

		return CustomUserDetails.from(user);
	}

	@Override
	public CustomUserDetails loadUserByUid(String uid) {
		log.debug("Loading user by UID: {}", uid);

		Users user = usersRepository.findByUserUuid(uid)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with UID: " + uid));

		return CustomUserDetails.from(user);
	}

	@Override
	public CustomUserDetails loadUserByEmail(String email) {
		log.debug("Loading user by email: {}", email);

		Users user = usersRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

		return CustomUserDetails.from(user);
	}

	@Override
	public CustomUserDetails loadUserByUsername(String username) {
		// username을 email로 사용
		return loadUserByEmail(username);
	}

	@Override
	@Transactional
	public CustomUserDetails register(RegisterRequest request) {
		log.debug("Registering new user with email: {}", request.getEmail());

		if (existsByEmail(request.getEmail())) {
			throw new IllegalArgumentException("User already exists with email: " + request.getEmail());
		}

		Role userRole = roleRepository.findByName("USER")
				.orElseThrow(() -> new IllegalStateException("Default USER role not found"));

		Users newUser = Users.builder()
				.email(request.getEmail())
				.password(passwordEncoder.encode(request.getPassword()))
				.name(request.getName())
				.username(request.getUsername())
				.roles(Set.of(userRole))
				.enabled(true)
				.build();

		Users savedUser = usersRepository.save(newUser);
		log.info("User registered successfully: {}", savedUser.getEmail());

		return CustomUserDetails.from(savedUser);
	}

	@Override
	public boolean existsByEmail(String email) {
		return usersRepository.existsByEmail(email);
	}

	@Override
	@Transactional
	public void logout(String username) {
		log.debug("Processing logout for user: {}", username);
		// 토큰 블랙리스트 처리 등의 로직을 여기에 구현
		log.info("User logged out successfully: {}", username);
	}
}