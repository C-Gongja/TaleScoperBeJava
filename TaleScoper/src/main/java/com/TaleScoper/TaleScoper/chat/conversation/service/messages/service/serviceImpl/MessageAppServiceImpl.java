package com.TaleScoper.TaleScoper.chat.conversation.service.messages.service.serviceImpl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.TaleScoper.TaleScoper.chat.conversation.entity.Conversation;
import com.TaleScoper.TaleScoper.chat.conversation.service.ConversationService;
import com.TaleScoper.TaleScoper.chat.conversation.service.messages.dto.MessageRequestDto;
import com.TaleScoper.TaleScoper.chat.conversation.service.messages.dto.MessageResponseDto;
import com.TaleScoper.TaleScoper.chat.conversation.service.messages.entity.Message;
import com.TaleScoper.TaleScoper.chat.conversation.service.messages.service.MessageAppService;
import com.TaleScoper.TaleScoper.chat.conversation.service.messages.service.MessageService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessageAppServiceImpl implements MessageAppService {
	private final ConversationService conversationService;
	private final MessageService messageService;
	// private final RagClient ragClient;
	// private final LlmClient llmClient;

	@Transactional
	@Override
	public MessageResponseDto processUserMessage(MessageRequestDto dto) {
		// 1) conversation 조회 또는 안전하게 생성 (findOrCreate)
		Conversation conv = conversationService.findOrCreateConversation(
				dto.getConversationUuid(), dto.getUserUuid());

		// 2) 사용자 메시지 저장
		Message userMessage = messageService.createUserMessage(conv, dto);

		// 3) RAG 호출(동기 or 비동기 선택) -> AI 응답
		// RagResponse ragResp = ragClient.query(conv.getId(), userMessage.getId(),
		// dto);
		String testString = "Hello World. This is a test ai output.";

		// 4) assistant 메시지 저장
		Message aiMessage = messageService.createAssistantMessage(conv, testString);

		// // 5) Title 자동 요약(동기 or 비동기). 보통 비동기 권장.
		// if (conv.getTitle() == null || conv.getTitle().isBlank() ||
		// isTempTitle(conv)) {
		// // 비동기로 처리하면 응답 지연 없음. 여기서는 예시로 동기 호출도 가능.
		// String newTitle = llmClient.summarizeConversationTitle(userMessage,
		// aiMessage);
		// conversationService.updateTitle(conv.getId(), newTitle);
		// }

		// 6) 응답 DTO 구성
		// return buildResponseDto(conv, userMessage, aiMessage, ragResp);
		return MessageResponseDto.builder()
				.userUuid(dto.getUserUuid())
				.conversationUuid(conv.getConversationUuid())
				.messageUuid(userMessage.getMessagesUuid()) // should userMes and aiMes have same mesID?
				.role("AI")
				.content(aiMessage.getContent())
				.build();
	}
}
