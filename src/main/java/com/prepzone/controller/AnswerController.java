package com.prepzone.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prepzone.dto.AnswerDTO;
import com.prepzone.service.AnswerService;
import com.prepzone.util.HttpStatusCode;
import com.prepzone.util.ResponseModel;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/answers")
public class AnswerController {

    @Autowired
    private AnswerService answerService;

    @Autowired
    private HttpStatusCode httpStatusCode;

    @PostMapping("/create")
    public ResponseEntity<ResponseModel<AnswerDTO>> createAnswer(
            @RequestHeader UUID userId,
            @RequestBody AnswerDTO answerDTO) {
        log.info("Begin AnswerController -> createAnswer()");
        ResponseModel<AnswerDTO> response = answerService.createAnswer(answerDTO, userId);
        log.info("End AnswerController -> createAnswer()");
        HttpStatus status = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
        return ResponseEntity.status(status).body(response);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ResponseModel<AnswerDTO>> updateAnswer(
            @PathVariable UUID id,
            @RequestHeader UUID userId,
            @RequestBody AnswerDTO answerDTO) {
        log.info("Begin AnswerController -> updateAnswer()");
        ResponseModel<AnswerDTO> response = answerService.updateAnswer(id, answerDTO, userId);
        log.info("End AnswerController -> updateAnswer()");
        HttpStatus status = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<ResponseModel<AnswerDTO>> getAnswer(@PathVariable UUID id) {
        log.info("Begin AnswerController -> getAnswer()");
        ResponseModel<AnswerDTO> response = answerService.getAnswerById(id);
        log.info("End AnswerController -> getAnswer()");
        HttpStatus status = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/question/{questionId}")
    public ResponseEntity<ResponseModel<List<AnswerDTO>>> getAnswersByQuestion(@PathVariable UUID questionId) {
        log.info("Begin AnswerController -> getAnswersByQuestion()");
        ResponseModel<List<AnswerDTO>> response = answerService.getAnswersByQuestion(questionId);
        log.info("End AnswerController -> getAnswersByQuestion()");
        HttpStatus status = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/lecturer/{lecturerId}")
    public ResponseEntity<ResponseModel<List<AnswerDTO>>> getAnswersByLecturer(@PathVariable UUID lecturerId) {
        log.info("Begin AnswerController -> getAnswersByLecturer()");
        ResponseModel<List<AnswerDTO>> response = answerService.getAnswersByLecturer(lecturerId);
        log.info("End AnswerController -> getAnswersByLecturer()");
        HttpStatus status = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
        return ResponseEntity.status(status).body(response);
    }

}
