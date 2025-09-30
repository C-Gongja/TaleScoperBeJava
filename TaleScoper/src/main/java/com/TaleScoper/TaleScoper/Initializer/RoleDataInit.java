package com.TaleScoper.TaleScoper.Initializer;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.TaleScoper.TaleScoper.role.entity.Role;
import com.TaleScoper.TaleScoper.role.repository.RoleRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RoleDataInit implements CommandLineRunner {
	private final RoleRepository roleRepository;

	@Override
	public void run(String... arg) throws Exception {
		if (!roleRepository.existsByName("USER")) {
			Role userRole = Role.builder()
					.name("USER")
					.description("regular user")
					.build();
			roleRepository.save(userRole);
		}

		if (!roleRepository.existsByName("ADMIN")) {
			Role adminRole = Role.builder()
					.name("ADMIN")
					.description("admin user")
					.build();
			roleRepository.save(adminRole);
		}
	}
}
