package com.prepzone.serviceimpl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.prepzone.dto.AnswerDTO;
import com.prepzone.entity.Answer;
import com.prepzone.entity.Question;
import com.prepzone.repository.AnswerRepository;
import com.prepzone.repository.QuestionRepository;
import com.prepzone.service.AnswerService;
import com.prepzone.util.ResponseModel;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AnswerServiceImpl implements AnswerService {

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Override
    public ResponseModel<AnswerDTO> createAnswer(AnswerDTO dto, UUID userId) {
        ResponseModel<AnswerDTO> response = new ResponseModel<>();
        try {
            log.info("Begin AnswerServiceImpl -> createAnswer()");

            Question question = questionRepository.findById(dto.getQuestionId())
                    .orElseThrow(() -> new EntityNotFoundException("Question not found"));

            Answer answer = new Answer();
            answer.setStudentId(dto.getStudentId());
            answer.setQuestion(question);
            answer.setSolution(dto.getSolution());
            answer.setPoints(dto.getPoints() != null ? dto.getPoints() : 0L);
            answer.setAttempts(dto.getAttempts() != null ? dto.getAttempts() : 0);
            answer.setLecturerId(dto.getLecturerId());
            answer.setCreatedBy(userId);

            Answer saved = answerRepository.save(answer);
            response.setData(mapToDTO(saved));
            response.setMessage("Answer saved successfully");
            response.setStatusCode(HttpStatus.CREATED.toString());

            log.info("End AnswerServiceImpl -> createAnswer()");
        } catch (Exception e) {
            log.error("Exception in AnswerServiceImpl -> createAnswer()", e);
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
        }
        return response;
    }

    @Override
    public ResponseModel<AnswerDTO> updateAnswer(UUID id, AnswerDTO dto, UUID userId) {
        ResponseModel<AnswerDTO> response = new ResponseModel<>();
        try {
            log.info("Begin AnswerServiceImpl -> updateAnswer()");

            Answer answer = answerRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Answer not found"));

            answer.setStudentId(dto.getStudentId());
            if (dto.getQuestionId() != null) {
                Question question = questionRepository.findById(dto.getQuestionId())
                        .orElseThrow(() -> new EntityNotFoundException("Question not found"));
                answer.setQuestion(question);
            }
            answer.setSolution(dto.getSolution());
            answer.setPoints(dto.getPoints() != null ? dto.getPoints() : answer.getPoints());
            answer.setAttempts(dto.getAttempts() != null ? dto.getAttempts() : answer.getAttempts());
            answer.setLecturerId(dto.getLecturerId());

            Answer updated = answerRepository.save(answer);
            response.setData(mapToDTO(updated));
            response.setMessage("Answer updated successfully");
            response.setStatusCode(HttpStatus.OK.toString());

            log.info("End AnswerServiceImpl -> updateAnswer()");
        } catch (Exception e) {
            log.error("Exception in AnswerServiceImpl -> updateAnswer()", e);
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
        }
        return response;
    }

    @Override
    public ResponseModel<AnswerDTO> getAnswerById(UUID id) {
        ResponseModel<AnswerDTO> response = new ResponseModel<>();
        try {
            log.info("Begin AnswerServiceImpl -> getAnswerById()");
            Answer answer = answerRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Answer not found"));
            response.setData(mapToDTO(answer));
            response.setMessage("Answer fetched successfully");
            response.setStatusCode(HttpStatus.OK.toString());
            log.info("End AnswerServiceImpl -> getAnswerById()");
        } catch (Exception e) {
            log.error("Exception in AnswerServiceImpl -> getAnswerById()", e);
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
        }
        return response;
    }

    @Override
    public ResponseModel<List<AnswerDTO>> getAnswersByQuestion(UUID questionId) {
        ResponseModel<List<AnswerDTO>> response = new ResponseModel<>();
        try {
            log.info("Begin AnswerServiceImpl -> getAnswersByQuestion()");
            List<AnswerDTO> dtos = answerRepository.findByQuestionId(questionId).stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
            response.setData(dtos);
            response.setMessage("Answers fetched successfully");
            response.setStatusCode(HttpStatus.OK.toString());
            log.info("End AnswerServiceImpl -> getAnswersByQuestion()");
        } catch (Exception e) {
            log.error("Exception in AnswerServiceImpl -> getAnswersByQuestion()", e);
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
        }
        return response;
    }

    @Override
    public ResponseModel<List<AnswerDTO>> getAnswersByLecturer(UUID lecturerId) {
        ResponseModel<List<AnswerDTO>> response = new ResponseModel<>();
        try {
            log.info("Begin AnswerServiceImpl -> getAnswersByLecturer()");
            List<AnswerDTO> dtos = answerRepository.findByLecturerId(lecturerId).stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
            response.setData(dtos);
            response.setMessage("Answers fetched successfully");
            response.setStatusCode(HttpStatus.OK.toString());
            log.info("End AnswerServiceImpl -> getAnswersByLecturer()");
        } catch (Exception e) {
            log.error("Exception in AnswerServiceImpl -> getAnswersByLecturer()", e);
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
        }
        return response;
    }


    private AnswerDTO mapToDTO(Answer answer) {
        return AnswerDTO.builder()
                .id(answer.getId())
                .studentId(answer.getStudentId())
                .questionId(answer.getQuestion().getId())
                .solution(answer.getSolution())
                .points(answer.getPoints())
                .attempts(answer.getAttempts())
                .lecturerId(answer.getLecturerId())
                .build();
    }
}
