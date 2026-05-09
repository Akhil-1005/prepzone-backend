package com.prepzone.entity;

import java.util.UUID;

import com.prepzone.util.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "answers")
@Getter
@Setter
public class Answer extends BaseEntity {

    @Column(nullable = false)
    private UUID studentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(length = 2000)
    private String solution;  // optional

    @Column(nullable = false)
    private Long points = 0L;      // default 0

    @Column(nullable = false)
    private Integer attempts = 0;  // default 0

    @Column(nullable = false)
    private UUID lecturerId;       // just store the lecturer ID directly
}
