package com.prepzone.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.prepzone.entity.Chapter;
import com.prepzone.response.ChapterBasicProjection;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, UUID> {
	List<ChapterBasicProjection> findBySubjectId(UUID subjectId);
}
