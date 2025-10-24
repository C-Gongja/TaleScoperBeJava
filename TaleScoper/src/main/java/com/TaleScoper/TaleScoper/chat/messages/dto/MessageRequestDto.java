package com.TaleScoper.TaleScoper.chat.messages.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class MessageRequestDto {
	private String conversationUuid;
	private String userUuid;
	private String role;
	private String content;
	// private Long lat;
	// private Long lng;
	// private IMAGE img;
	// private String language;
	// User Query
	// private String text;
}
