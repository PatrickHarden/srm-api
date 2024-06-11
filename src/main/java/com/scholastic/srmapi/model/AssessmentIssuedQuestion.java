package com.scholastic.srmapi.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Model representation of a single SRM question assigned out to a single student's assessment.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "assessment_issued_questions",
        indexes = {
        @Index(name = "ix_assess_id", columnList = "assessment_id"),
        @Index(name = "ix_assessment_question_id", columnList = "assessment_question_id")}
)
public class AssessmentIssuedQuestion implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private int answerType;

    private Date answeredTime;

    @Column(nullable = false)
    private int correctAnsDisplay;

    @Column(nullable = false)
    private int correctAnsIndex;

    @CreatedDate
    private Date created;

    @Column(nullable = false)
    private int endLexile;

    @Column(nullable = false, precision = 10, scale = 6)
    private BigDecimal endSigma;

    @LastModifiedDate
    private Date modified;

    @Column(nullable = false)
    private int questionLexile;

    @Column(nullable = false)
    private int questionSeq;

    @Column(nullable = false)
    private int questionType;

    @Column(nullable = false)
    private boolean skipped;

    @Column(nullable = false)
    private int startLexile;

    @Column(nullable = false, precision = 10, scale = 6)
    private BigDecimal startSigma;

    @Column(nullable = false)
    private int studentAnsDisplay;

    @Column(nullable = false)
    private int studentAnsIndex;

    @Column(nullable = false)
    private boolean success;

    @Column(nullable = false, length = 5)
    private int timeElapsedSeconds;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_id")
    private AssessmentTestDetails assessment;

    @ManyToOne
    @JoinColumn(name = "assessment_question_id")
    private AssessmentQuestion assessmentQuestion;

}
