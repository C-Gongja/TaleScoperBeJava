package com.TaleScoper.TaleScoper.RAGClient.config;

import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.channel.ChannelOption;
// read-timeout handled via HttpClient.responseTimeout
import reactor.netty.http.client.HttpClient;

@Configuration
public class RAGClientConfig {

    private static final Logger log = LoggerFactory.getLogger(RAGClientConfig.class);

    @Value("${python.rag.server.local-url:http://127.0.0.1:8000}")
    private String baseUrl;

    @Value("${python.rag.server.connect-timeout-ms:2000}")
    private int connectTimeoutMs;

    @Value("${python.rag.server.read-timeout-ms:10000}")
    private int readTimeoutMs;

    @Bean
    public WebClient pythonWebClient() {
        log.info("Creating Python WebClient with base URL: {}", baseUrl);

    // Configure HttpClient with connect and response timeouts (non-deprecated)
    HttpClient httpClient = HttpClient.create()
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeoutMs)
        .responseTimeout(Duration.ofMillis(readTimeoutMs));

        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
