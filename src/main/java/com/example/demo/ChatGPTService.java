package com.example.demo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ChatGPTService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${openai.api.model}")
    private String model;

    public ChatGPTService(WebClient.Builder webClientBuilder,
                          @Value("${openai.api.key}") String apiKey,
                          @Value("${openai.api.url}") String apiUrl) {
        this.webClient = webClientBuilder
                .baseUrl(apiUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public Mono<String> getChatResponse(String message) {
        String requestBody = String.format("""
            {
                "model": "%s",
                "messages": [{"role": "user", "content": "%s"}],
                "max_tokens": 500
            }
            """, model, message);

        return webClient.post()
                .uri("/chat/completions")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .map(this::extractResponse)
                .onErrorReturn("Error processing request");
    }

    public Flux<String> getChatStreamResponse(String message) {
        String requestBody = String.format("""
            {
                "model": "%s",
                "messages": [{"role": "user", "content": "%s"}],
                "stream": true,
                "max_tokens": 500
            }
            """, model, message);

        return webClient.post()
                .uri("/chat/completions")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToFlux(String.class)
                .map(this::parseStreamResponse)
                .onErrorResume(e -> Flux.just("data: [ERROR]\n\n"));
    }

    private String extractResponse(String response) {
        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            return jsonNode.get("choices").get(0).get("message").get("content").asText();
        } catch (Exception e) {
            return "Error parsing response";
        }
    }

    private String parseStreamResponse(String line) {
        if (line.startsWith("data: ") && !line.contains("[DONE]")) {
            return line + "\n\n";
        }
        return "";
    }
}