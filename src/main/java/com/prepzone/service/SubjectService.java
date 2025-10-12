package com.prepzone.service;

import java.util.UUID;

import com.prepzone.entity.Subject;
import com.prepzone.util.ResponseModel;

public interface SubjectService {

	ResponseModel<Subject> createSubject(Subject subject, UUID userId);

}
