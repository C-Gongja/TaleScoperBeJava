package com.TaleScoper.TaleScoper.RAGClient.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.TaleScoper.TaleScoper.RAGClient.dto.ChatRequest;
import com.TaleScoper.TaleScoper.RAGClient.dto.ChatResponse;
import com.TaleScoper.TaleScoper.RAGClient.dto.CustomChatRequest;
import com.TaleScoper.TaleScoper.RAGClient.service.RAGChatService;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/python")
@RequiredArgsConstructor
public class RAGChatController {

    private final RAGChatService pythonChatService;

    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chatSync(@RequestBody ChatRequest request) {
        ChatResponse resp = pythonChatService.chat(request);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/chat/async")
    public Mono<ChatResponse> chatAsync(@RequestBody ChatRequest request) {
        return pythonChatService.chatAsync(request);
    }

    @PostMapping("/chat/custom")
    public Mono<ChatResponse> customChat(@RequestBody CustomChatRequest request) {
        return pythonChatService.customChat(request);
    }

    @GetMapping("/health")
    public ResponseEntity<Boolean> health() {
        return ResponseEntity.ok(pythonChatService.isHealthy());
    }
}
