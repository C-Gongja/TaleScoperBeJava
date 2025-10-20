package com.TaleScoper.TaleScoper.user.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.TaleScoper.TaleScoper.role.entity.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Users {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	private Long id;

	@Column(unique = true, nullable = false, length = 36)
	private String userUuid; // UUID 필드

	@Column(nullable = false)
	private String name;
	// private String firstName;

	// @Column(nullable = false)
	// private String lastName;

	@Column(nullable = true)
	private String username;

	@Column(nullable = false)
	private String email;

	@Column(nullable = true)
	private String password;

	// 계정 상태 필드들
	@Builder.Default
	@Column(name = "enabled", nullable = false)
	private Boolean enabled = true;

	@Builder.Default
	@Column(name = "account_non_expired", nullable = false)
	private Boolean accountNonExpired = true;

	@Builder.Default
	@Column(name = "account_non_locked", nullable = false)
	private Boolean accountNonLocked = true;

	@Builder.Default
	@Column(name = "credentials_non_expired", nullable = false)
	private Boolean credentialsNonExpired = true;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	@Builder.Default
	private Set<Role> roles = new HashSet<>();

	@CreationTimestamp
	@Column(nullable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@PrePersist
	public void generateUid() {
		if (this.userUuid == null) {
			this.userUuid = UUID.randomUUID().toString();
		}
	}

	// 계정 상태 확인 메서드들
	public boolean isEnabled() {
		return Boolean.TRUE.equals(enabled);
	}

	public boolean isAccountNonExpired() {
		return Boolean.TRUE.equals(accountNonExpired);
	}

	public boolean isAccountNonLocked() {
		return Boolean.TRUE.equals(accountNonLocked);
	}

	public boolean isCredentialsNonExpired() {
		return Boolean.TRUE.equals(credentialsNonExpired);
	}
}
