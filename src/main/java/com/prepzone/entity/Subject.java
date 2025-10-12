package com.prepzone.entity;

import java.sql.Timestamp;
import java.util.UUID;

import com.prepzone.util.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class Subject extends BaseEntity{
	

	private String subjectName;

}
