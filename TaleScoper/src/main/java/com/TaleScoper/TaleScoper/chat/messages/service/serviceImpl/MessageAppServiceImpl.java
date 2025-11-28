package com.TaleScoper.TaleScoper.chat.messages.service.serviceImpl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.TaleScoper.TaleScoper.chat.conversation.entity.Conversation;
import com.TaleScoper.TaleScoper.chat.conversation.service.ConversationService;
import com.TaleScoper.TaleScoper.chat.messages.dto.MessageRequestDto;
import com.TaleScoper.TaleScoper.chat.messages.dto.MessageResponseDto;
import com.TaleScoper.TaleScoper.chat.messages.entity.Message;
import com.TaleScoper.TaleScoper.chat.messages.service.MessageAppService;
import com.TaleScoper.TaleScoper.chat.messages.service.MessageService;
import com.TaleScoper.TaleScoper.RAGClient.dto.ChatRequest;
import com.TaleScoper.TaleScoper.RAGClient.dto.ChatResponse;
import com.TaleScoper.TaleScoper.RAGClient.service.RAGChatService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@RequiredArgsConstructor
@Slf4j
public class MessageAppServiceImpl implements MessageAppService {
	private final ConversationService conversationService;
	private final MessageService messageService;
	private final RAGChatService pythonChatService;

	// private final RagClient ragClient;
	// private final LlmClient llmClient;

	@Transactional
	@Override
	public MessageResponseDto processUserMessage(MessageRequestDto dto) {
		// 1) conversation 조회 또는 안전하게 생성 (findOrCreate)
		Conversation conv = conversationService.findOrCreateConversation(
				dto.getConversationUuid(), dto.getUserUuid());

		// 2) 사용자 메시지 저장
		messageService.userNewMessage(conv, dto);

		// 3) Call Python FastAPI /chat endpoint (synchronous example)
		String aiReply;
		try {
			ChatRequest chatReq = new ChatRequest();
			chatReq.setMessage(dto.getMessage());
			chatReq.setImageBase64(dto.getImageBase64());
			chatReq.setMaxTokens(1000);

			// Synchronous blocking call (will block the current thread)
			ChatResponse chatResp = pythonChatService.chat(chatReq);
			aiReply = chatResp != null ? chatResp.getReply() : null;
		} catch (Exception ex) {
			log.warn("Python chat call failed, falling back to default reply: {}", ex.getMessage());
			// Fallback text if external service fails
			aiReply = "(AI unavailable) Sorry, I can't answer right now.";
		}

		if (aiReply == null) {
			aiReply = "(AI returned no reply)";
		}

		// 4) AI assistant 메시지 저장
		Message aiMessage = messageService.createAssistantMessage(conv, aiReply);

		// Alternative: non-blocking (async) approach
		// Use this if you don't want to block the caller. It will save the assistant
		// message when the Python API responds, but processUserMessage returns earlier.
		// Uncomment and adapt if you have a mechanism to inform clients later (websocket, SSE, push):
		//
		// ChatRequest chatReq = new ChatRequest();
		// chatReq.setUserId(dto.getUserUuid());
		// chatReq.setMessage(dto.getContent());
		// pythonChatService.chatAsync(chatReq)
		//     .subscribe(resp -> {
		//         String reply = resp != null ? resp.getReply() : "(no reply)";
		//         messageService.createAssistantMessage(conv, reply);
		//     }, err -> {
		//         log.warn("Async python chat failed: {}", err.getMessage());
		//     });

		// 5) 응답 DTO 구성
		return MessageResponseDto.builder()
				.userUuid(dto.getUserUuid())
				.conversationUuid(conv.getConversationUuid())
				.messageUuid(aiMessage.getMessagesUuid())
				.role("AI")
				.content(aiMessage.getContent())
				.build();
	}
}
