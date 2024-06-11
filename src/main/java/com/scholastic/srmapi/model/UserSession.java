package com.scholastic.srmapi.model;

import com.scholastic.aa.api.enums.TeacherJudgement;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserSession {
    private User user;
    private AssessmentTestDetails assessmentTestDetails;
    private TeacherJudgement teacherJudgement;
    private BigDecimal lexileScore;

}
