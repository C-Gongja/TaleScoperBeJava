package com.TaleScoper.TaleScoper.chat.conversation.service;

import java.util.List;
import java.util.Optional;

import com.TaleScoper.TaleScoper.chat.conversation.entity.Conversation;

public interface ConversationService {
	public Conversation createConversation(String userUuid, String title);

	public List<Conversation> getUserConversations(String userUuid);

	public Optional<Conversation> getConversation(String ConversationUuid);

	public Conversation findOrCreateConversation(String conversationUuid, String userUuid);

	public void updateTitle(Long convId, String newTitle);
}
