package com.prepzone.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.prepzone.entity.Question;

@Repository
public interface QuestionRepository extends JpaRepository<Question, UUID> {
   

	List<Question> findByChapter_Id(UUID chapterId);
}
