package com.prepzone.entity;

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
public class Chapter  extends BaseEntity{
  
	
	private String subjectName;
	private String chapterName;
}
