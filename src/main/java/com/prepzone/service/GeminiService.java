package com.prepzone.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    @Value("${app.gemini.apiKey}")
    private String apiKey;

    private static final String GEMINI_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

    private static final String PROMPT =
            "Extract all text from this image exactly as it appears. " +
            "For all mathematical expressions, equations, and formulas use LaTeX notation: " +
            "wrap inline math in $...$ and block/display equations in $$...$$. " +
            "For chemical equations use standard chemical notation. " +
            "Preserve the original question structure and numbering if present. " +
            "Return only the extracted text with no additional explanations or commentary.";

    private final RestTemplate restTemplate = new RestTemplate();

    public String extractTextFromImage(String imageBase64, String mimeType) {
        String url = GEMINI_URL + "?key=" + apiKey;

        Map<String, Object> inlineData = Map.of("mime_type", mimeType, "data", imageBase64);
        Map<String, Object> imagePart = Map.of("inline_data", inlineData);
        Map<String, Object> textPart = Map.of("text", PROMPT);
        Map<String, Object> content = Map.of("parts", List.of(imagePart, textPart));
        Map<String, Object> generationConfig = Map.of("temperature", 0.1, "maxOutputTokens", 2048);
        Map<String, Object> requestBody = Map.of(
                "contents", List.of(content),
                "generationConfig", generationConfig
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

        List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.getBody().get("candidates");
        Map<String, Object> responseContent = (Map<String, Object>) candidates.get(0).get("content");
        List<Map<String, Object>> parts = (List<Map<String, Object>>) responseContent.get("parts");
        return (String) parts.get(0).get("text");
    }
}
