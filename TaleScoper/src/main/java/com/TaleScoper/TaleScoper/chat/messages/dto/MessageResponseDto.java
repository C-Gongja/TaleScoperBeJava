package com.TaleScoper.TaleScoper.chat.messages.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class MessageResponseDto {
	private String userUuid;
	private String conversationUuid;
	private String messageUuid;
	private String role;
	private String content;
}
