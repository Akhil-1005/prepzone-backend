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
public class OptionDTO {
    private UUID id;
    private String optionText;
    private Boolean isCorrect;
}
