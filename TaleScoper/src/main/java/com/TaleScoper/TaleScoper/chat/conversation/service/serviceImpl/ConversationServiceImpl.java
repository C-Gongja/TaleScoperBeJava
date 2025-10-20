package com.TaleScoper.TaleScoper.chat.conversation.service.serviceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.TaleScoper.TaleScoper.chat.conversation.entity.Conversation;
import com.TaleScoper.TaleScoper.chat.conversation.repository.ConversationRepository;
import com.TaleScoper.TaleScoper.chat.conversation.service.ConversationService;
import com.TaleScoper.TaleScoper.user.entity.Users;
import com.TaleScoper.TaleScoper.user.repository.UsersRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ConversationServiceImpl implements ConversationService {

	private final ConversationRepository conversationRepository;
	private final UsersRepository usersRepository;

	@Override
	public Conversation createConversation(String userUuid, String title) {
		Users user = usersRepository.findByUserUuid(userUuid).orElseThrow(() -> new RuntimeException("User not found"));
		Conversation conversation = Conversation.builder()
				.userId(user.getId())
				.userUuid(userUuid)
				.title(title)
				.build();
		return conversationRepository.save(conversation);
	}

	@Override
	public List<Conversation> getUserConversations(String userUuid) {
		return conversationRepository.findAllByUserUuid(userUuid);
	}

	@Override
	public Optional<Conversation> getConversation(String ConversationUuid) {
		return conversationRepository.findByConversationUuid(ConversationUuid);
	}

	@Override
	@Transactional
	public Conversation findOrCreateConversation(String conversationUuid, String userUuid) {
		if (conversationUuid != null) {
			return conversationRepository.findByConversationUuid(conversationUuid)
					.orElseThrow(() -> new IllegalArgumentException(
							"Conversation not found"));
		} else {
			// 안전한 생성: 단순 save (여기서 race condition 방지하려면 DB unique 제약 + 예외 핸들링)
			Users user = usersRepository.findByUserUuid(userUuid).orElseThrow(() -> new IllegalArgumentException(
					"user not found"));

			Conversation conv = Conversation.builder()
					.userId(user.getId())
					.userUuid(userUuid)
					.title("New Chat")
					.build();
			return conversationRepository.save(conv);
		}
	}

	@Override
	@Transactional
	public void updateTitle(Long convId, String newTitle) {
		conversationRepository.findById(convId).ifPresent(conv -> {
			conv.setTitle(newTitle);
			conversationRepository.save(conv);
		});
	}
}
