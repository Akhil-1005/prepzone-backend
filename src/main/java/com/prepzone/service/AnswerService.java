package com.prepzone.service;

import java.util.List;
import java.util.UUID;

import com.prepzone.dto.AnswerDTO;
import com.prepzone.util.ResponseModel;

public interface AnswerService {

    ResponseModel<AnswerDTO> createAnswer(AnswerDTO answerDTO, UUID userId);

    ResponseModel<AnswerDTO> updateAnswer(UUID id, AnswerDTO answerDTO, UUID userId);

    ResponseModel<AnswerDTO> getAnswerById(UUID id);

    ResponseModel<List<AnswerDTO>> getAnswersByQuestion(UUID questionId);

    ResponseModel<List<AnswerDTO>> getAnswersByLecturer(UUID lecturerId);

}
