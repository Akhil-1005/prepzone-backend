package com.prepzone.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.prepzone.dto.SubjectSummaryDTO;
import com.prepzone.entity.Subject;

public interface SubjectRepository extends JpaRepository<Subject, UUID> {

    List<Subject> findByCreatedBy(UUID userId);

    @Query("SELECT new com.prepzone.dto.SubjectSummaryDTO(s.id, s.subjectName, COUNT(DISTINCT c.id), COUNT(DISTINCT q.id)) " +
           "FROM Subject s " +
           "LEFT JOIN Chapter c ON c.subject = s " +
           "LEFT JOIN Question q ON q.chapter = c " +
           "WHERE s.createdBy = :userId " +
           "GROUP BY s.id, s.subjectName")
    List<SubjectSummaryDTO> findSubjectSummariesByUserId(@Param("userId") UUID userId);
}
