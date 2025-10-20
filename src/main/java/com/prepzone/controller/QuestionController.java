package com.prepzone.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prepzone.dto.QuestionDTO;
import com.prepzone.service.QuestionService;
import com.prepzone.util.HttpStatusCode;
import com.prepzone.util.ResponseModel;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/question")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private HttpStatusCode httpStatusCode;

    @PostMapping("/create")
    public ResponseEntity<ResponseModel<QuestionDTO>> createQuestion(
            @RequestHeader UUID userId,
            @RequestBody QuestionDTO questionDTO) {

        log.info("Begin QuestionController -> createQuestion()");
        ResponseModel<QuestionDTO> response = questionService.createQuestion(questionDTO, userId);
        log.info("End QuestionController -> createQuestion()");
        HttpStatus status = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
        return ResponseEntity.status(status).body(response);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ResponseModel<QuestionDTO>> updateQuestion(
            @PathVariable UUID id,
            @RequestHeader UUID userId,
            @RequestBody QuestionDTO questionDTO) {

        log.info("Begin QuestionController -> updateQuestion()");
        ResponseModel<QuestionDTO> response = questionService.updateQuestion(id, questionDTO, userId);
        log.info("End QuestionController -> updateQuestion()");
        HttpStatus status = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<ResponseModel<QuestionDTO>> getQuestion(@PathVariable UUID id) {
        log.info("Begin QuestionController -> getQuestion()");
        ResponseModel<QuestionDTO> response = questionService.getQuestionById(id);
        log.info("End QuestionController -> getQuestion()");
        HttpStatus status = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/chapter/{chapterId}")
    public ResponseEntity<ResponseModel<List<QuestionDTO>>> getQuestionsByChapter(@PathVariable UUID chapterId) {
        log.info("Begin QuestionController -> getQuestionsByChapter()");
        ResponseModel<List<QuestionDTO>> response = questionService.getQuestionsByChapter(chapterId);
        log.info("End QuestionController -> getQuestionsByChapter()");
        HttpStatus status = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
        return ResponseEntity.status(status).body(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseModel<String>> deleteQuestion(@PathVariable UUID id) {
        log.info("Begin QuestionController -> deleteQuestion()");
        ResponseModel<String> response = questionService.deleteQuestion(id);
        log.info("End QuestionController -> deleteQuestion()");
        HttpStatus status = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
        return ResponseEntity.status(status).body(response);
    }
}
