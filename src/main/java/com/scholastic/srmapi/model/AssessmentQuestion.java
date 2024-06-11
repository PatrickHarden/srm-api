package com.scholastic.srmapi.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;

/**
 * Model representation of a possible question that can be provided by the SRM.
 */
@Getter
@Setter
@Entity
@Cacheable
@Table(name = "assessment_questions", indexes = {
    @Index(name = "ix_assessnebt_questions_lexile_pool_active", columnList = "lexile,pool,active,suppressed") })
public class AssessmentQuestion implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String sourceId;

  @Column(name="answer_2", nullable = false, length = 1024)
  private String answer2;

  @Column(name="answer_3", nullable = false, length = 1024)
  private String answer3;

  @Column(name="answer_4", nullable = false, length = 1024)
  private String answer4;

  @Column(nullable = false, length = 1024)
  private String answerCorrect;

  @Column(length = 1024)
  private String credit;

  @Column(nullable = false, length = 22)
  private double lexile;

  @Column(nullable = false, length = 1024)
  private String passage;

  @Column(nullable = false, length = 32)
  private String pool;

  @Column(nullable = false, length = 1024)
  private String question;

  @Column(nullable = false, length = 0)
  private boolean active = true;

  @Column(nullable = false, length = 0)
  private boolean suppressed = false;

}