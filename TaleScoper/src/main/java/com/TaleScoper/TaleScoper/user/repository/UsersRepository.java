package com.TaleScoper.TaleScoper.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.TaleScoper.TaleScoper.user.entity.Users;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {
	Optional<Users> findByUsername(String username);

	Optional<Users> findByUserUuid(String uid);

	Boolean existsByUsername(String username);

	Optional<Users> findByEmail(String email);

	Boolean existsByEmail(String email);
}
