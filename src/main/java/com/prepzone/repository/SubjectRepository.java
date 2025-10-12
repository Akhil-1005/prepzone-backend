package com.prepzone.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prepzone.entity.Subject;

public interface SubjectRepository  extends JpaRepository<Subject, UUID>{

}
