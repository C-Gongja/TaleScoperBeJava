package com.TaleScoper.TaleScoper.RAGClient.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CustomChatRequest {
    @JsonProperty("session_id")
    private String sessionId;

    @JsonProperty("payload")
    private Object payload;
}
