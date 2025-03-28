package com.example.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

@RestController
@CrossOrigin
@RequestMapping("/chat")
public class ChatController {

    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${openai.api.model}")
    private String model;

    public ChatController(WebClient.Builder webClientBuilder,
                          @Value("${openai.api.key}") String apiKey,
                          @Value("${openai.api.url}") String baseUrl) {
        this.webClient = webClientBuilder
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamResponse(@RequestParam String message) {
        String requestBody = String.format("""
            {
                "model": "%s",
                "messages": [{"role": "user", "content": "%s"}],
                "stream": true,
                "max_tokens": 500
            }
            """, model, message.replace("\"", "\\\""));

        return webClient.post()
                .uri("/chat/completions")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToFlux(String.class)
                .filter(line -> line.startsWith("data: ") && !line.contains("[DONE]"))
                .map(line -> {
                    try {
                        String jsonStr = line.substring(5).trim();
                        JsonNode node = objectMapper.readTree(jsonStr);
                        String content = node.path("choices")
                                .path(0)
                                .path("delta")
                                .path("content")
                                .asText();
                        return "data: " + (content != null ? content : "") + "\n\n";
                    } catch (Exception e) {
                        return "data: [ERROR] " + e.getMessage() + "\n\n";
                    }
                });
    }
}