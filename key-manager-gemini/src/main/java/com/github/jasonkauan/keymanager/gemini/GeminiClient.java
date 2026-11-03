package com.github.jasonkauan.keymanager.gemini;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.jasonkauan.keymanager.KeyManager;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import java.util.List;
import java.util.Map;

public class GeminiClient {
    private final RestClient http = RestClient.create("https://generativelanguage.googleapis.com");
    private final KeyManager keys;
    private final String model;

    public GeminiClient(KeyManager keys, String model) {
        this.keys = keys;
        this.model = model;
    }

    public String generate(String prompt) {
        JsonNode res = keys.execute(key -> http.post()
                .uri("/v1beta/models/{m}:generateContent", model)
                .header("x-goog-api-key", key.value())
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("contents", List.of(Map.of("parts", List.of(Map.of("text", prompt))))))
                .retrieve()
                .body(JsonNode.class));
        return res.at("/candidates/0/content/parts/0/text").asText();
    }
}