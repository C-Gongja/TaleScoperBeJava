package com.TaleScoper.TaleScoper.chat.conversation.entity;

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
public class Conversation {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	private Long id;

	@Column(unique = true, nullable = false, length = 36)
	private String conversationUuid;

	@Column(nullable = false)
	private Long userId;

	@Column(nullable = false)
	private String userUuid;

	private String title;

	@CreationTimestamp
	@Column(nullable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	private LocalDateTime updatedAt;

	@PrePersist
	public void generateUid() {
		if (this.conversationUuid == null) {
			this.conversationUuid = UUID.randomUUID().toString();
		}
	}
}
