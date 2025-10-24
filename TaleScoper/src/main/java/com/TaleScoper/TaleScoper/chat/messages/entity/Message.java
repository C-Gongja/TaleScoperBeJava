package com.TaleScoper.TaleScoper.chat.messages.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	private Long id;

	@Column(unique = true, nullable = false, length = 36)
	private String messagesUuid;

	@Column(nullable = false, length = 36)
	private String conversationUuid;

	@Column(nullable = false, length = 36)
	private String userUuid;

	@Column(nullable = false)
	private String role;

	private String content;

	@Column(nullable = true)
	private String image_url;

	// private JsonB metadata;

	// private Long categoryId; // 관광지/음식 등 관련 카테고리 (optional)
	// private Long targetId; // 특정 entity (예: 불국사 ID 등)

	@CreationTimestamp
	@Column(nullable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	private LocalDateTime updatedAt;

	@PrePersist
	public void generateUid() {
		if (this.messagesUuid == null) {
			this.messagesUuid = UUID.randomUUID().toString();
		}
	}
}
