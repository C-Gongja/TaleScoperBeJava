package com.TaleScoper.TaleScoper.RAGClient.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.TaleScoper.TaleScoper.RAGClient.dto.ChatRequest;
import com.TaleScoper.TaleScoper.RAGClient.dto.ChatResponse;
import com.TaleScoper.TaleScoper.RAGClient.dto.CustomChatRequest;

import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class RAGChatService {

    private final WebClient pythonWebClient;

    /**
     * Synchronous POST /chat (blocks)
     */
    @Retryable(value = { Exception.class }, maxAttempts = 3, backoff = @Backoff(delay = 2000))
    public ChatResponse chat(ChatRequest request) {
        try {
            return pythonWebClient.post()
                    .uri("/chat")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(ChatResponse.class)
                    .block();
        } catch (WebClientResponseException ex) {
            log.error("Python /chat failed: status={} body={}", ex.getStatusCode(), ex.getResponseBodyAsString());
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error calling Python /chat: {}", ex.getMessage(), ex);
        throw ex;
        }
    }

    // Optional fallback could be added by declaring a separate @Recover method
    // public ChatResponse recover(Exception ex, ChatRequest request) { ... }

    /**
     * Asynchronous POST /chat returning Mono<ChatResponse>
     */
    public Mono<ChatResponse> chatAsync(ChatRequest request) {
        return pythonWebClient.post()
                .uri("/chat")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ChatResponse.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)).filter(t -> !(t instanceof WebClientResponseException)))
                .doOnError(WebClientResponseException.class, ex -> log.error("Async Python /chat returned error: {} -> {}", ex.getStatusCode(), ex.getResponseBodyAsString()))
                .doOnError(ex -> log.error("Async Python /chat unexpected error: {}", ex.getMessage(), ex));
    }

    /**
     * Custom chat endpoint
     */
    public Mono<ChatResponse> customChat(CustomChatRequest request) {
        return pythonWebClient.post()
                .uri("/chat/custom")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ChatResponse.class)
                .retryWhen(Retry.fixedDelay(2, Duration.ofMillis(500)).filter(t -> !(t instanceof WebClientResponseException)))
                .doOnError(ex -> log.error("Python /chat/custom error: {}", ex.getMessage(), ex));
    }

    /**
     * Health check GET /health
     */
    public boolean isHealthy() {
        try {
            return pythonWebClient.get()
                    .uri("/health")
                    .retrieve()
                    .bodyToMono(String.class)
                    .map(s -> s != null && !s.isBlank())
                    .blockOptional()
                    .orElse(false);
        } catch (WebClientResponseException ex) {
            log.warn("Python /health responded with status: {} body: {}", ex.getStatusCode(), ex.getResponseBodyAsString());
            return false;
        } catch (Exception ex) {
            log.warn("Error calling Python /health: {}", ex.getMessage());
            return false;
        }
    }
}
