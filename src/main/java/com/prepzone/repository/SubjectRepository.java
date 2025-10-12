package com.prepzone.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prepzone.entity.Subject;

public interface SubjectRepository  extends JpaRepository<Subject, UUID>{

	List<Subject> findByCreatedBy(UUID userId);

}
