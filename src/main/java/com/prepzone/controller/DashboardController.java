package com.prepzone.controller;

import com.prepzone.dto.DashboardStatsDTO;
import com.prepzone.service.DashboardService;
import com.prepzone.util.HttpStatusCode;
import com.prepzone.util.ResponseModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final HttpStatusCode httpStatusCode;

    @GetMapping("/stats")
    public ResponseEntity<ResponseModel<DashboardStatsDTO>> getStats(@RequestHeader UUID userId) {
        log.info("Begin DashboardController -> getStats()");
        ResponseModel<DashboardStatsDTO> response = dashboardService.getStats(userId);
        log.info("End DashboardController -> getStats()");
        return ResponseEntity.status(httpStatusCode.getHttpStatusFromCode(response.getStatusCode())).body(response);
    }
}
