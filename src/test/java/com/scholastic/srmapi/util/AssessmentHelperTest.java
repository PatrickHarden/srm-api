package com.scholastic.srmapi.util;

import com.scholastic.srmapi.helper.AssessmentBuilderHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class AssessmentHelperTest {

    @Test
    @DisplayName("Get current questions for assessment with missing pieces")
    void getCurrentQuestionForAssessment_missingFieldsReturnNull() {
        var assessment = AssessmentBuilderHelper.buildBaseAssessmentDetails();
        assessment.setIssuedQuestions(null);
        assertThat(AssessmentHelper.getCurrentQuestionForAssessment(assessment)).isNull();

        var assessmentQuestion = AssessmentBuilderHelper.buildIssuedQuestion();
        assessment.setCurrentQuestion(-1);
        assessment.setIssuedQuestions(List.of(assessmentQuestion));

        assertThat(AssessmentHelper.getCurrentQuestionForAssessment(assessment)).isNull();

    }
}
