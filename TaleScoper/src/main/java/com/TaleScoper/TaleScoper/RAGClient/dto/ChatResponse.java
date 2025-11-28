package com.TaleScoper.TaleScoper.RAGClient.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ChatResponse {
    @JsonProperty("reply")
    private String reply;

    @JsonProperty("metadata")
    private Metadata metadata;

    @Data
    public static class Metadata {
        private String model;
        
        @JsonProperty("prompt_tokens")
        private Integer promptTokens;
        
        @JsonProperty("completion_tokens")
        private Integer completionTokens;
        
        @JsonProperty("total_tokens")
        private Integer totalTokens;
    }
}
