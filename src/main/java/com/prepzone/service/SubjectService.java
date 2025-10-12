package com.prepzone.service;

import java.util.List;
import java.util.UUID;

import com.prepzone.entity.Subject;
import com.prepzone.util.ResponseModel;

public interface SubjectService {

	ResponseModel<Subject> createSubject(Subject subject, UUID userId);

	ResponseModel<List<Subject>> getAllSubjects(UUID userId);

	ResponseModel<Subject> getSubjectById(UUID id);

	ResponseModel<Subject> updateSubject(UUID id, Subject subject, UUID userId);

	ResponseModel<String> deleteSubject(UUID id);

}
