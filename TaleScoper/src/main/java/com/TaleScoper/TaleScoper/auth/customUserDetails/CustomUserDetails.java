package com.TaleScoper.TaleScoper.auth.customUserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.TaleScoper.TaleScoper.role.entity.Role;
import com.TaleScoper.TaleScoper.user.entity.Users;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {
	// Spring Security가 필요로 하는 핵심 정보만
	private Long id;
	private String uid;
	private String username; // 로그인 ID (보통 email)
	private String email;

	@JsonIgnore
	private String password; // 인증용 (평소엔 사용 안함)
	private Set<Role> roles;

	// Spring Security 상태 플래그들
	private boolean enabled;
	private boolean accountNonExpired;
	private boolean accountNonLocked;
	private boolean credentialsNonExpired;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return roles.stream()
				.map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
				.collect(Collectors.toSet());
	}

	@Override
	public String getPassword() {
		return password; // 주로 null 또는 빈 문자열
	}

	@Override
	public String getUsername() {
		return email; // 이메일을 username으로 사용
	}

	@Override
	public boolean isAccountNonExpired() {
		return accountNonExpired;
	}

	@Override
	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	// User Entity → UserPrincipal 변환
	public static CustomUserDetails from(Users user) {
		return CustomUserDetails.builder()
				.id(user.getId())
				.uid(user.getUid()) // UID 매핑
				.username(user.getEmail())
				.email(user.getEmail())
				.password("") // 보안상 빈 문자열로 설정
				.roles(user.getRoles())
				.enabled(
						true)// .enabled(user.isActive() && user.isEmailVerified())
				.accountNonExpired(true)
				.accountNonLocked(true)
				.credentialsNonExpired(true)
				.build();
	}
}
