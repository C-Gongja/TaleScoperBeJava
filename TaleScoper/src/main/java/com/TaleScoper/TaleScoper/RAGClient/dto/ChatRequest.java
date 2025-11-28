package com.TaleScoper.TaleScoper.RAGClient.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ChatRequest {
    // Required field
    private String message;
    
    // Optional fields (can be null)
    @JsonProperty("image_base64")
    private String imageBase64;
    
    private Double temperature;
    
    @JsonProperty("max_tokens")
    private Integer maxTokens;
}
