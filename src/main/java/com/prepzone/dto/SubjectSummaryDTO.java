package com.prepzone.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubjectSummaryDTO {
    private UUID id;
    private String subjectName;
    private long chapterCount;
    private long questionCount;
}
