package com.scholastic.srmapi.model.vo;

import com.scholastic.aa.api.IAssessment;
import com.scholastic.aa.api.enums.TestState;
import com.scholastic.aa.api.enums.TestType;
import com.scholastic.srmapi.model.AssessmentTestDetails;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * Representation of an Assessment which can be leveraged by the Assessment Algorithm.
 */
@Getter
public class AssessmentVO implements IAssessment {

    private Date date;
    private Long id;
    private BigDecimal measure;
    private TestState state;
    private Map<String, String> stateValues;
    private TestType type;
    private Integer numSkipsTaken;

    public static AssessmentVO build(AssessmentTestDetails assessment) {
        if (assessment == null) {
            return null;
        }

        var assessmentVO = new AssessmentVO();
        assessmentVO.date = assessment.getModifiedDate();
        assessmentVO.id = assessment.getId();
        assessmentVO.measure = BigDecimal.valueOf(assessment.getLexile());
        assessmentVO.state = assessment.getTestState();
        assessmentVO.stateValues = assessment.getStateValues();
        assessmentVO.type = assessment.getTestType();
        assessmentVO.numSkipsTaken = assessment.getNumberSkipsTaken();
        return assessmentVO;
    }
}
