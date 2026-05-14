package com.prepzone.controller;

import com.prepzone.request.TextExtractRequest;
import com.prepzone.service.GeminiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TextExtractController {

    private final GeminiService geminiService;

    @PostMapping("/question/extract-text")
    public ResponseEntity<Map<String, String>> extractText(@RequestBody TextExtractRequest request) {
        log.info("Begin TextExtractController -> extractText()");
        String text = geminiService.extractTextFromImage(request.getImageBase64(), request.getMimeType());
        log.info("End TextExtractController -> extractText()");
        return ResponseEntity.ok(Map.of("text", text));
    }
}
