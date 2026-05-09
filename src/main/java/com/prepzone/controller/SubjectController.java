package com.prepzone.controller;

import java.util.List;
import java.util.UUID;

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

import com.prepzone.entity.Subject;
import com.prepzone.service.SubjectService;
import com.prepzone.util.HttpStatusCode;
import com.prepzone.util.ResponseModel;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/subject")
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
	
	
	   // ✅ READ - Get All
	@GetMapping("/getAll")
	public ResponseEntity<ResponseModel<List<Subject>>> getAllSubjects(@RequestHeader UUID userId) {
	    log.info("Begin SubjectController -> getAllSubjects()");
	    ResponseModel<List<Subject>> response = subjectService.getAllSubjects(userId);
	    log.info("End SubjectController -> getAllSubjects()");
	    HttpStatus httpStatus = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
	    return ResponseEntity.status(httpStatus).body(response);
	}


    @GetMapping("/get/{id}")
    public ResponseEntity<ResponseModel<Subject>> getSubjectById(@PathVariable UUID id) {
        log.info("Begin SubjectController -> getSubjectById()");
        ResponseModel<Subject> response = subjectService.getSubjectById(id);
        log.info("End SubjectController -> getSubjectById()");
        HttpStatus httpStatus = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
        return ResponseEntity.status(httpStatus).body(response);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ResponseModel<Subject>> updateSubject(
            @PathVariable UUID id,
            @RequestHeader UUID userId,
            @RequestBody Subject subject) {

        log.info("Begin SubjectController -> updateSubject()");
        ResponseModel<Subject> response = subjectService.updateSubject(id, subject, userId);
        log.info("End SubjectController -> updateSubject()");
        HttpStatus httpStatus = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
        return ResponseEntity.status(httpStatus).body(response);
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseModel<String>> deleteSubject(@PathVariable UUID id) {
        log.info("Begin SubjectController -> deleteSubject()");
        ResponseModel<String> response = subjectService.deleteSubject(id);
        log.info("End SubjectController -> deleteSubject()");
        HttpStatus httpStatus = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
        return ResponseEntity.status(httpStatus).body(response);
    }

}
