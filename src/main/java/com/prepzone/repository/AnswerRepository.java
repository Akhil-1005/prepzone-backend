package com.prepzone.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prepzone.entity.Answer;

public interface AnswerRepository extends JpaRepository<Answer, UUID> {
    List<Answer> findByQuestionId(UUID questionId);
    List<Answer> findByLecturerId(UUID lecturerId);
}
