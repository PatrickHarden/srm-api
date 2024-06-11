package com.scholastic.srmapi.response;

import com.scholastic.srmapi.config.EnvironmentConfig;
import com.scholastic.srmapi.helper.AssessmentBuilderHelper;
import com.scholastic.srmapi.util.Constants;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AssessmentTestResponseTest {

    static MockedStatic<EnvironmentConfig> envStatic;


    @AfterAll
    static void afterAll() {
        envStatic.close();
    }

    @BeforeAll
    static void setup() {
        envStatic = Mockito.mockStatic(EnvironmentConfig.class);
        envStatic.when(() -> EnvironmentConfig.getSystemVar(Constants.SHUFFLE_ASSESSMENT_ANSWERS)).thenReturn("false");
    }

    @Test
    @DisplayName("Retrieve an assessment with shuffled questions disabled will return answers in expected order.")
    void getAssessmentWithShuffledQuestionsDisabled_orderOfQuestionsVerified() {
        var assessmentTestDetails = AssessmentBuilderHelper.buildFullAssessmentDetails();
        assessmentTestDetails.setTestState(null);
        var assessmentTestResponse = AssessmentTestResponse.buildAssessmentResponse(assessmentTestDetails);

        assertThat(assessmentTestResponse).isNotNull();
        assertThat(assessmentTestResponse.getQuestion()).isNotNull();

        var issuedQuestion = assessmentTestDetails.getIssuedQuestions().stream()
                .filter(q -> assessmentTestDetails.getCurrentQuestion() == q.getQuestionSeq())
                .findFirst().get();

        var actualAnswers = assessmentTestResponse.getQuestion().getAnswers().stream().map(AssessmentAnswerResponse::getLabel).toList();
        var expectedAssessmentValues = List.of(issuedQuestion.getAssessmentQuestion().getAnswerCorrect(),
                issuedQuestion.getAssessmentQuestion().getAnswer2(),
                issuedQuestion.getAssessmentQuestion().getAnswer3(),
                issuedQuestion.getAssessmentQuestion().getAnswer4());

        assertThat(actualAnswers).containsExactlyElementsOf(expectedAssessmentValues);
    }

    @Test
    @DisplayName("Retrieve an assessment with the second answer being correct and the order of answers is correct")
    void getAssessmentWithAdjustedQuestionOrderQ1_orderOfQuestionsVerified() {
        var assessmentTestDetails = AssessmentBuilderHelper.buildFullAssessmentDetails();
        assessmentTestDetails.setIssuedQuestions(AssessmentBuilderHelper.buildFullQuestionsList());
        assessmentTestDetails.setCurrentQuestion(1);

        var assessmentTestResponse = AssessmentTestResponse.buildAssessmentResponse(assessmentTestDetails);

        assertThat(assessmentTestResponse).isNotNull();
        assertThat(assessmentTestResponse.getQuestion()).isNotNull();

        var actualAnswers = assessmentTestResponse.getQuestion().getAnswers().stream().map(AssessmentAnswerResponse::getId).toList();
        var expectedAssessmentIds = List.of(1,0,2,3);

        assertThat(actualAnswers).containsExactlyElementsOf(expectedAssessmentIds);
    }

    @Test
    @DisplayName("Retrieve an assessment with the third answer being correct and the order of answers is correct")
    void getAssessmentWithAdjustedQuestionOrderQ2_orderOfQuestionsVerified() {
        var assessmentTestDetails = AssessmentBuilderHelper.buildFullAssessmentDetails();
        assessmentTestDetails.setIssuedQuestions(AssessmentBuilderHelper.buildFullQuestionsList());
        assessmentTestDetails.setCurrentQuestion(2);

        var assessmentTestResponse = AssessmentTestResponse.buildAssessmentResponse(assessmentTestDetails);

        assertThat(assessmentTestResponse).isNotNull();
        assertThat(assessmentTestResponse.getQuestion()).isNotNull();

        var actualAnswers = assessmentTestResponse.getQuestion().getAnswers().stream().map(AssessmentAnswerResponse::getId).toList();
        var expectedAssessmentIds = List.of(2,1,0,3);

        assertThat(actualAnswers).containsExactlyElementsOf(expectedAssessmentIds);
    }

    @Test
    @DisplayName("Retrieve an assessment with the fourth answer being correct and the order of answers is correct")
    void getAssessmentWithAdjustedQuestionOrderQ3_orderOfQuestionsVerified() {
        var assessmentTestDetails = AssessmentBuilderHelper.buildFullAssessmentDetails();
        assessmentTestDetails.setIssuedQuestions(AssessmentBuilderHelper.buildFullQuestionsList());
        assessmentTestDetails.setCurrentQuestion(3);

        var assessmentTestResponse = AssessmentTestResponse.buildAssessmentResponse(assessmentTestDetails);

        assertThat(assessmentTestResponse).isNotNull();
        assertThat(assessmentTestResponse.getQuestion()).isNotNull();

        var actualAnswers = assessmentTestResponse.getQuestion().getAnswers().stream().map(AssessmentAnswerResponse::getId).toList();
        var expectedAssessmentIds = List.of(3, 1, 2, 0);

        assertThat(actualAnswers).containsExactlyElementsOf(expectedAssessmentIds);
    }
}
