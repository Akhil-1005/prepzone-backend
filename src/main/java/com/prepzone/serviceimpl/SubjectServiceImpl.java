package com.prepzone.serviceimpl;

import java.util.List;
import java.util.Optional;
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
	@Override
	public ResponseModel<List<Subject>> getAllSubjects(UUID userId) {
	    ResponseModel<List<Subject>> response = new ResponseModel<>();

	    try {
	        log.info("Begin SubjectServiceImpl -> getAllSubjects()");
	        List<Subject> subjects = subjectRepository.findByCreatedBy(userId);

	        response.setData(subjects);
	        response.setStatusCode(HttpStatus.OK.toString());
	        response.setMessage("Fetched all subjects created by user successfully");
	        log.info("End of SubjectServiceImpl -> getAllSubjects()");
	    } catch (Exception e) {
	        log.error("Exception in SubjectServiceImpl -> getAllSubjects()", e);
	        response.setData(null);
	        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
	        response.setMessage(e.getMessage());
	    }

	    return response;
	}
	   @Override
	    public ResponseModel<Subject> getSubjectById(UUID id) {
	        ResponseModel<Subject> response = new ResponseModel<>();

	        try {
	            log.info("Begin SubjectServiceImpl -> getSubjectById()");
	            Optional<Subject> subjectOpt = subjectRepository.findById(id);

	            if (subjectOpt.isPresent()) {
	                response.setData(subjectOpt.get());
	                response.setStatusCode(HttpStatus.OK.toString());
	                response.setMessage("Subject fetched successfully");
	            } else {
	                response.setData(null);
	                response.setStatusCode(HttpStatus.NOT_FOUND.toString());
	                response.setMessage("Subject not found");
	            }

	            log.info("End of SubjectServiceImpl -> getSubjectById()");
	        } catch (Exception e) {
	            log.error("Exception in SubjectServiceImpl -> getSubjectById()", e);
	            response.setData(null);
	            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
	            response.setMessage(e.getMessage());
	        }

	        return response;
	    }

	    @Override
	    public ResponseModel<Subject> updateSubject(UUID id, Subject subject, UUID userId) {
	        ResponseModel<Subject> response = new ResponseModel<>();

	        try {
	            log.info("Begin SubjectServiceImpl -> updateSubject()");
	            Optional<Subject> existingSubjectOpt = subjectRepository.findById(id);

	            if (existingSubjectOpt.isPresent()) {
	                Subject existingSubject = existingSubjectOpt.get();
	                existingSubject.setSubjectName(subject.getSubjectName());

	                Subject updatedSubject = subjectRepository.save(existingSubject);

	                response.setData(updatedSubject);
	                response.setStatusCode(HttpStatus.OK.toString());
	                response.setMessage("Subject updated successfully");
	            } else {
	                response.setData(null);
	                response.setStatusCode(HttpStatus.NOT_FOUND.toString());
	                response.setMessage("Subject not found");
	            }

	            log.info("End of SubjectServiceImpl -> updateSubject()");
	        } catch (Exception e) {
	            log.error("Exception in SubjectServiceImpl -> updateSubject()", e);
	            response.setData(null);
	            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
	            response.setMessage(e.getMessage());
	        }

	        return response;
	    }

	    @Override
	    public ResponseModel<String> deleteSubject(UUID id) {
	        ResponseModel<String> response = new ResponseModel<>();

	        try {
	            log.info("Begin SubjectServiceImpl -> deleteSubject()");
	            Optional<Subject> subjectOpt = subjectRepository.findById(id);

	            if (subjectOpt.isPresent()) {
	                subjectRepository.deleteById(id);
	                response.setData("Deleted Successfully");
	                response.setStatusCode(HttpStatus.OK.toString());
	                response.setMessage("Subject deleted successfully");
	            } else {
	                response.setData(null);
	                response.setStatusCode(HttpStatus.NOT_FOUND.toString());
	                response.setMessage("Subject not found");
	            }

	            log.info("End of SubjectServiceImpl -> deleteSubject()");
	        } catch (Exception e) {
	            log.error("Exception in SubjectServiceImpl -> deleteSubject()", e);
	            response.setData(null);
	            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
	            response.setMessage(e.getMessage());
	        }

	        return response;
	    }


}
