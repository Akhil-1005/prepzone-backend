package com.prepzone.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerDTO {
    private UUID id;
    private UUID studentId;
    private UUID questionId;
    private String solution;
    private Long points;
    private Integer attempts;
    private UUID lecturerId;
}
