package com.prepzone.service;

import com.prepzone.dto.DashboardStatsDTO;
import com.prepzone.util.ResponseModel;

import java.util.UUID;

public interface DashboardService {
    ResponseModel<DashboardStatsDTO> getStats(UUID userId);
}
