package com.TaleScoper.TaleScoper.chat.conversation.service.messages.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.TaleScoper.TaleScoper.chat.conversation.service.messages.entity.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
	// 최신 메시지부터 (채팅에서 일반적)
	Page<Message> findByConversationUuidOrderByCreatedAtDesc(
			String conversationUuid,
			Pageable pageable);

	// 오래된 메시지부터
	Page<Message> findByConversationUuidOrderByCreatedAtAsc(
			String conversationUuid,
			Pageable pageable);

	// 커서 기반 - 특정 시간 이전 메시지
	List<Message> findByConversationUuidAndCreatedAtBeforeOrderByCreatedAtDesc(
			String conversationUuid,
			LocalDateTime cursor,
			Pageable pageable);

	// 메시지 개수 조회
	Long countByConversationUuid(String conversationUuid);
}
