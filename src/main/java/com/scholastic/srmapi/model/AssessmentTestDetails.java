package com.scholastic.srmapi.model;

import com.scholastic.aa.api.enums.TestState;
import com.scholastic.aa.api.enums.TestType;
import com.scholastic.srmapi.util.AssessmentHelper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Model representation of a single student's SRM assessment, regardless of state.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "assessment_test",
        indexes = {
        @Index(name = "assessment_test_student_id_idx", columnList = "sourceId")
})
public class AssessmentTestDetails implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int currentQuestion;

    private Date assignedDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedDate;

    private String description;

    @Column(nullable = false)
    private int endLexile;

    @Column(nullable = false, precision = 10, scale = 6)
    private BigDecimal endSigma;

    @Column(nullable = false)
    private double lexile;

    @Column(nullable = false)
    private int numberSkipsPossible;

    @Column(name = "num_skipped_questions")
    private Integer numberSkipsTaken;

    @Column(nullable = false)
    private int practiceQuestionsPossible;

    @Column(name = "num_practice_questions")
    private Integer practiceQuestionsTaken;

    private Integer schoolYear;

    @Column(nullable = false)
    private int startLexile;

    @Column(nullable = false, precision = 10, scale = 6)
    private BigDecimal startSigma;

    private Long sourceId;

    @Enumerated(EnumType.STRING)
    private TestState testState;

    @Enumerated(EnumType.STRING)
    private TestType testType;

    @Column(precision = 8, scale = 4)
    private BigDecimal uncertainity;

    private Long userLoginSessionId;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "assessment", cascade = CascadeType.ALL)
    @BatchSize(size = 40)
    private List<AssessmentIssuedQuestion> issuedQuestions = new ArrayList<>(0);

    @ElementCollection
    @MapKeyColumn(name = "name")
    @Column(name = "value")
    @CollectionTable(name = "assessment_test_state", joinColumns = { @JoinColumn(name = "assessment_id") })
    private Map<String, String> stateValues = new HashMap<>();

    @PrePersist
    protected void onCreate() {
        this.setCreatedDate(AssessmentHelper.getCurrentDateTime());
    }

    @PreUpdate
    protected void onUpdate() {
        this.setModifiedDate(AssessmentHelper.getCurrentDateTime());
    }
}
