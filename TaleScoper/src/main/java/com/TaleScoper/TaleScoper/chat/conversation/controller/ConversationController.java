package com.TaleScoper.TaleScoper.chat.conversation.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.TaleScoper.TaleScoper.chat.conversation.entity.Conversation;
import com.TaleScoper.TaleScoper.chat.conversation.service.ConversationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/chat/conversation")
@RequiredArgsConstructor
public class ConversationController {

	private final ConversationService conversationService;

	@PostMapping
	public ResponseEntity<Conversation> createConversation(
			@RequestParam String userUuid,
			@RequestParam(required = false) String title) {
		Conversation conversation = conversationService.createConversation(userUuid, title);
		return ResponseEntity.ok(conversation);
	}

	@GetMapping("/user/{userUuid}")
	public ResponseEntity<List<Conversation>> getUserConversations(@PathVariable String userUuid) {
		return ResponseEntity.ok(conversationService.getUserConversations(userUuid));
	}

	@GetMapping("/{conversationUuid}")
	public ResponseEntity<Conversation> getConversation(@PathVariable String conversationUuid) {
		return conversationService.getConversation(
				conversationUuid)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}
}
