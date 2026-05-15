package com.prepzone.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class KeepAliveScheduler {

    @Value("${app.selfUrl:}")
    private String selfUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @Scheduled(fixedDelay = 840000)
    public void keepAlive() {
        if (selfUrl == null || selfUrl.isBlank()) {
            return;
        }
        try {
            restTemplate.getForObject(selfUrl + "/prepzone/health", String.class);
            log.info("Keep-alive ping successful → {}", selfUrl);
        } catch (Exception e) {
            log.warn("Keep-alive ping failed: {}", e.getMessage());
        }
    }
}
