package com.prepzone.dto;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDTO {
    private UUID id;
    private String questionText;
    private String explanation;
    private String difficulty;
    private UUID chapterId;
    private List<OptionDTO> options;
}
