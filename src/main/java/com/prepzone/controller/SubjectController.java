package com.prepzone.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.prepzone.entity.Subject;
import com.prepzone.service.SubjectService;
import com.prepzone.util.HttpStatusCode;
import com.prepzone.util.ResponseModel;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@AllArgsConstructor
public class SubjectController {

	private final SubjectService subjectService;
	private final HttpStatusCode httpStatusCode;

	@PostMapping("/create")
	public ResponseEntity<ResponseModel<Subject>> creteSubject(@RequestHeader UUID userId,
			@RequestBody Subject subject) {
		log.info("Begin SubjectController -> creteSubject()");
		ResponseModel<Subject> response = subjectService.createSubject(subject, userId);
		log.info("End SubjectController -> creteSubject()");
		HttpStatus httpStatus = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
		return ResponseEntity.status(httpStatus).body(response);
	}

}
