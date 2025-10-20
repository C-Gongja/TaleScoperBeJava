package com.TaleScoper.TaleScoper.chat.conversation.service.messages.service;

import com.TaleScoper.TaleScoper.chat.conversation.service.messages.dto.MessageRequestDto;
import com.TaleScoper.TaleScoper.chat.conversation.service.messages.dto.MessageResponseDto;

/* 
 * Conversation and Message Orchestrator
 * Use Case Service Layer
 * Senario Level Service
*/

public interface MessageAppService {
	public MessageResponseDto processUserMessage(MessageRequestDto dto);
}
