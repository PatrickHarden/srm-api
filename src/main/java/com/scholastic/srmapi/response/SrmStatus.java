package com.scholastic.srmapi.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;

@Getter
@Setter
public class SrmStatus {
    private Integer id;
    private Integer studentId;
    private Integer teacherId;
    private String benchmark;
    private Date assignedDate;
    private Date completedDate;
    private BigDecimal lexile;
    private String status;
    private Integer assessmentTest;
    private Date endDate;
    private Long adminAssignmentId;
    private String teacherJudgement;
    private Integer cancelledByUserId;
    private Instant modifiedDate;
    private int currentQuestion;
    private long timeSpent;
}
