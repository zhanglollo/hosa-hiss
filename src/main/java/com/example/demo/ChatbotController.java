package com.example.demo;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import java.time.Duration;

@RestController
public class ChatbotController {

    private final ClaudeAIService claudeAIService;

    public ChatbotController(ClaudeAIService claudeAIService) {
        this.claudeAIService = claudeAIService;
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamChatResponse(@RequestParam String message) {
        return claudeAIService.getChatResponse(message)
                .flatMapMany(response -> {
                    // Split the response into lines and stream them
                    String[] lines = response.split("\n");
                    return Flux.fromArray(lines);
                })
                .delayElements(Duration.ofMillis(500)) // Simulate streaming
                .onErrorResume(e -> {
                    // Handle errors and return a fallback message
                    return Flux.just("An error occurred: " + e.getMessage());
                });
    }
}