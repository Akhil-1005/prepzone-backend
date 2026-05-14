package com.prepzone.serviceimpl;

import com.prepzone.dto.DashboardStatsDTO;
import com.prepzone.dto.SubjectSummaryDTO;
import com.prepzone.repository.SubjectRepository;
import com.prepzone.service.DashboardService;
import com.prepzone.util.ResponseModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final SubjectRepository subjectRepository;

    @Override
    public ResponseModel<DashboardStatsDTO> getStats(UUID userId) {
        List<SubjectSummaryDTO> subjects = subjectRepository.findSubjectSummariesByUserId(userId);

        long subjectCount  = subjects.size();
        long chapterCount  = subjects.stream().mapToLong(SubjectSummaryDTO::getChapterCount).sum();
        long questionCount = subjects.stream().mapToLong(SubjectSummaryDTO::getQuestionCount).sum();

        DashboardStatsDTO stats = DashboardStatsDTO.builder()
                .subjectCount(subjectCount)
                .chapterCount(chapterCount)
                .questionCount(questionCount)
                .subjects(subjects)
                .build();

        return ResponseModel.<DashboardStatsDTO>builder()
                .statusCode("200 OK")
                .message("Dashboard stats fetched successfully")
                .data(stats)
                .build();
    }
}
