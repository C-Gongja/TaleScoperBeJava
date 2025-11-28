package com.TaleScoper.TaleScoper.chat.messages.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;

import com.TaleScoper.TaleScoper.chat.conversation.entity.Conversation;
import com.TaleScoper.TaleScoper.chat.messages.dto.MessageRequestDto;
import com.TaleScoper.TaleScoper.chat.messages.entity.Message;

public interface MessageService {

	public Message userNewMessage(Conversation conv, MessageRequestDto dto);

	public Message createAssistantMessage(Conversation conv, String content);

	public Message saveMessage(MessageRequestDto message);

	public Page<Message> getMessagesByConversation(String conversationUuid, int page, int size);

	public List<Message> getRecentMessages(String conversationUuid, int limit);

	public List<Message> getMessagesBefore(String conversationUuid, LocalDateTime cursor, int limit);
}
