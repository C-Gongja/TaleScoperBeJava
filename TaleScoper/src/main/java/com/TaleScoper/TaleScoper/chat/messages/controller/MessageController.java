package com.TaleScoper.TaleScoper.chat.messages.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.TaleScoper.TaleScoper.chat.messages.dto.MessageRequestDto;
import com.TaleScoper.TaleScoper.chat.messages.dto.MessageResponseDto;
import com.TaleScoper.TaleScoper.chat.messages.entity.Message;
import com.TaleScoper.TaleScoper.chat.messages.service.MessageAppService;
import com.TaleScoper.TaleScoper.chat.messages.service.MessageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/chat/message")
@RequiredArgsConstructor
public class MessageController {

	private final MessageAppService messageAppService;
	private final MessageService messageService;

	@PostMapping
	public ResponseEntity<MessageResponseDto> createMessage(@RequestBody MessageRequestDto dto) {
		MessageResponseDto resp = messageAppService.processUserMessage(dto);
		return ResponseEntity.status(HttpStatus.CREATED).body(resp);
	}

	// @PostMapping
	// public ResponseEntity<MessageResponseDto> createMessage(@RequestBody
	// MessageRequestDto messageDto) {
	// Message newMessage = messageService.saveMessage(messageDto);
	// MessageResponseDto newMessageDto = MessageResponseDto.builder()
	// .userUuid(newMessage.getUserUuid())
	// .conversationUuid(newMessage.getConversationUuid())
	// .messageUuid(newMessage.getMessagesUuid())
	// .content(newMessage.getContent())
	// .build();

	// return ResponseEntity.status(HttpStatus.CREATED).body(newMessageDto);
	// }

	// 페이징 조회
	@GetMapping("/conversation/{conversationUuid}")
	public ResponseEntity<Page<MessageResponseDto>> getMessages(
			@PathVariable String conversationUuid,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size) {

		Page<Message> messagePage = messageService.getMessagesByConversation(conversationUuid, page, size);

		Page<MessageResponseDto> responsePage = messagePage.map(msg -> MessageResponseDto.builder()
				.messageUuid(msg.getMessagesUuid())
				.conversationUuid(msg.getConversationUuid())
				.userUuid(msg.getUserUuid())
				.role(msg.getRole())
				.content(msg.getContent())
				.build());

		return ResponseEntity.ok(responsePage);
	}

	// 최신 메시지 조회
	@GetMapping("/conversation/{conversationUuid}/recent")
	public ResponseEntity<List<MessageResponseDto>> getRecentMessages(
			@PathVariable String conversationUuid,
			@RequestParam(defaultValue = "50") int limit) {

		List<Message> messages = messageService.getRecentMessages(conversationUuid, limit);

		List<MessageResponseDto> responseDtos = messages.stream()
				.map(msg -> MessageResponseDto.builder()
						.messageUuid(msg.getMessagesUuid())
						.conversationUuid(msg.getConversationUuid())
						.userUuid(msg.getUserUuid())
						.role(msg.getRole())
						.content(msg.getContent())
						.build())
				.collect(Collectors.toList());

		return ResponseEntity.ok(responseDtos);
	}

	// 커서 기반 조회 (무한 스크롤용)
	@GetMapping("/conversation/{conversationUuid}/before")
	public ResponseEntity<List<MessageResponseDto>> getMessagesBefore(
			@PathVariable String conversationUuid,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime before,
			@RequestParam(defaultValue = "20") int limit) {

		List<Message> messages = messageService.getMessagesBefore(conversationUuid, before, limit);

		List<MessageResponseDto> responseDtos = messages.stream()
				.map(msg -> MessageResponseDto.builder()
						.messageUuid(msg.getMessagesUuid())
						.conversationUuid(msg.getConversationUuid())
						.userUuid(msg.getUserUuid())
						.role(msg.getRole())
						.content(msg.getContent())
						.build())
				.collect(Collectors.toList());

		return ResponseEntity.ok(responseDtos);
	}
}
