package com.prepzone.service;

import java.util.List;
import java.util.UUID;

import com.prepzone.dto.QuestionDTO;
import com.prepzone.util.ResponseModel;

public interface QuestionService {

    ResponseModel<QuestionDTO> createQuestion(QuestionDTO questionDTO, UUID userId);

    ResponseModel<QuestionDTO> updateQuestion(UUID id, QuestionDTO questionDTO, UUID userId);

    ResponseModel<QuestionDTO> getQuestionById(UUID id);

    ResponseModel<List<QuestionDTO>> getQuestionsByChapter(UUID chapterId);

    ResponseModel<String> deleteQuestion(UUID id);
}
