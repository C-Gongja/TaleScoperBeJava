package com.TaleScoper.TaleScoper.chat.messages.service;

import com.TaleScoper.TaleScoper.chat.messages.dto.MessageRequestDto;
import com.TaleScoper.TaleScoper.chat.messages.dto.MessageResponseDto;

/* 
 * Conversation and Message Orchestrator
 * Use Case Service Layer
 * Senario Level Service
*/

public interface MessageAppService {
	public MessageResponseDto processUserMessage(MessageRequestDto dto);
}
