package com.example.demo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ClaudeAIService {

    @Value("${claude.api.key}")
    private String apiKey;

    @Value("${claude.api.url}")
    private String apiUrl;

    private final WebClient webClient;

    public ClaudeAIService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl(apiUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public Mono<String> getChatResponse(String message) {
        String requestBody = String.format("""
                {
                    "prompt": "%s",
                    "model": "claude-2.1",
                    "max_tokens": 500
                }
                """, message);

        return webClient.post()
                .uri("/complete")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> {
                    // Parse the response to extract the chatbot's reply
                    return response; // Replace with actual parsing logic
                });
    }
}