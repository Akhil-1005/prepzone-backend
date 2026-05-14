package com.prepzone.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DashboardStatsDTO {
    private long subjectCount;
    private long chapterCount;
    private long questionCount;
    private List<SubjectSummaryDTO> subjects;
}
