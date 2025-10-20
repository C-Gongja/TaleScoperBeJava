package com.TaleScoper.TaleScoper.chat.conversation.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.TaleScoper.TaleScoper.chat.conversation.entity.Conversation;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
	@Override
	Optional<Conversation> findById(Long id);

	Optional<Conversation> findByConversationUuid(String uuid);

	List<Conversation> findAllByUserId(Long userId);

	List<Conversation> findAllByUserUuid(String uuid);
}
