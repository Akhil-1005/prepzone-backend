package com.prepzone.serviceimpl;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.prepzone.entity.Subject;
import com.prepzone.repository.SubjectRepository;
import com.prepzone.service.SubjectService;
import com.prepzone.util.ResponseModel;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class SubjectServiceImpl implements SubjectService {

	private final SubjectRepository subjectRepository;

	@Override
	public ResponseModel<Subject> createSubject(Subject subject, UUID userId) {
		ResponseModel<Subject> response = new ResponseModel<>();

		try {
			log.info("Begin SubjectServiceImpl -> createSubject()");
			subject.setCreatedBy(userId);
			Subject savedSubject = subjectRepository.save(subject);

			response.setData(savedSubject);
			response.setStatusCode(HttpStatus.CREATED.toString());
			response.setMessage("Subject Info saved Sucessfully");
			log.info("End of SubjectServiceImpl -> createSubject()");
		} catch (Exception e) {
			log.info("Exception in SubjectServiceImpl -> createSubject()",e.getMessage());
			response.setData(null);
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			response.setMessage(e.getMessage());
		}
		return response;
	}

}
