package com.example.demo;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;

@RestController
public class ChatbotController {

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamChatResponse(@RequestParam String message) {
        // Simulate a streaming response (replace with your AI logic)
        return Flux.interval(Duration.ofMillis(500))
                .map(sequence -> "Bot: This is a streaming response to: " + message + " (part " + sequence + ")\n");
    }
}

