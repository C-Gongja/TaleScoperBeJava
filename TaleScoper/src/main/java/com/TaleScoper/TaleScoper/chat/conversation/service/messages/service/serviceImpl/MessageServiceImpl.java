package com.TaleScoper.TaleScoper.chat.conversation.service.messages.service.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.TaleScoper.TaleScoper.chat.conversation.entity.Conversation;
import com.TaleScoper.TaleScoper.chat.conversation.repository.ConversationRepository;
import com.TaleScoper.TaleScoper.chat.conversation.service.messages.dto.MessageRequestDto;
import com.TaleScoper.TaleScoper.chat.conversation.service.messages.entity.Message;
import com.TaleScoper.TaleScoper.chat.conversation.service.messages.repository.MessageRepository;
import com.TaleScoper.TaleScoper.chat.conversation.service.messages.service.MessageService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MessageServiceImpl implements MessageService {

	private final MessageRepository messageRepository;
	private final ConversationRepository conversationRepository;

	@Override
	public Message createUserMessage(Conversation conv, MessageRequestDto dto) {
		Message msg = Message.builder()
				.conversationUuid(
						conv.getConversationUuid())
				.userUuid(
						conv.getUserUuid())
				.role("user")
				.content(dto.getContent())
				.createdAt(LocalDateTime.now())
				.build();
		return messageRepository.save(msg);
	}

	@Override
	public Message createAssistantMessage(Conversation conv, String content) {
		Message msg = Message.builder()
				.conversationUuid(conv
						.getConversationUuid())
				.userUuid(
						conv.getUserUuid())
				.role("assistant")
				.content(content)
				.createdAt(LocalDateTime.now())
				.build();
		return messageRepository.save(msg);
	}

	@Transactional
	@Override
	public Message saveMessage(MessageRequestDto messageDto) {
		// Conversation이 실제로 존재하는지 검증
		Conversation conversation = conversationRepository
				.findByConversationUuid(messageDto.getConversationUuid())
				.orElseThrow(() -> new IllegalArgumentException(
						"Conversation not found with uuid: " + messageDto.getConversationUuid()));

		// 2. User가 해당 Conversation의 소유자인지 검증 (선택사항)
		if (!conversation.getUserUuid().equals(messageDto.getUserUuid())) {
			throw new IllegalArgumentException("User is not authorized for this conversation");
		}

		// 3. 입력값 검증 (null 체크 등)
		if (messageDto.getContent() == null || messageDto.getContent().trim().isEmpty()) {
			throw new IllegalArgumentException("Message content cannot be empty");
		}

		// Builder 패턴을 사용하여 안전하게 Message 생성
		Message newMessage = Message.builder()
				.conversationUuid(messageDto.getConversationUuid())
				.userUuid(messageDto.getUserUuid())
				.role(messageDto.getRole())
				// .content(messageDto.getContent().trim()) // 공백 제거??
				.content(messageDto.getContent())
				.build();

		return messageRepository.save(newMessage);
	}

	@Override
	public Page<Message> getMessagesByConversation(String conversationUuid, int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
		return messageRepository.findByConversationUuidOrderByCreatedAtDesc(conversationUuid, pageable);
	}

	@Override
	public List<Message> getRecentMessages(String conversationUuid, int limit) {
		Pageable pageable = PageRequest.of(0, limit, Sort.by("createdAt").descending());
		return messageRepository.findByConversationUuidOrderByCreatedAtDesc(conversationUuid, pageable)
				.getContent();
	}

	@Override
	public List<Message> getMessagesBefore(String conversationUuid, LocalDateTime cursor, int limit) {
		Pageable pageable = PageRequest.of(0, limit);
		return messageRepository.findByConversationUuidAndCreatedAtBeforeOrderByCreatedAtDesc(
				conversationUuid, cursor, pageable);
	}
}
