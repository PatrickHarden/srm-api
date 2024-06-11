package com.scholastic.srmapi.assessment.callback;

import com.scholastic.aa.api.enums.TestState;
import com.scholastic.aa.api.util.AssessmentStateValueConstants;
import com.scholastic.srmapi.model.User;
import com.scholastic.srmapi.model.UserSession;
import com.scholastic.srmapi.model.vo.AssessmentQuestionVO;
import com.scholastic.srmapi.model.vo.AssessmentVO;
import com.scholastic.srmapi.service.AssessmentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LitproUSAssessmentCallbackTest {

    @InjectMocks
    private LitproUSAssessmentCallback callback;

    @Mock
    private AssessmentService assessmentService;

    @Mock
    private AssessmentQuestionCallback questionCallback;

    @Mock
    private AssessmentResumeCallback resumeCallback;

    @Mock
    private AssessmentBeginCallback beginCallback;

    @Mock
    private UserSession userSession;

    @Test
    @DisplayName("Get Last Test callback successfully returns assessment")
    void getLastTestForAssessmentType_assessmentReturnsSuccessfully() {
        var assessment = AssessmentBuilderHelper.buildFullAssessmentDetails();
        var translatedAssessment = AssessmentVO.build(assessment);
        var user = new User();
        user.setSourceId(assessment.getSourceId());
        when(userSession.getUser()).thenReturn(user);

        when(assessmentService.getAssessmentsByUserIdAndTestState(assessment.getSourceId(), TestState.IN_PROGRESS))
                .thenReturn(List.of(assessment));
        var returnedAssessment = callback.getLastTest(TestState.IN_PROGRESS);
        assertThat(returnedAssessment).usingRecursiveComparison().isEqualTo(translatedAssessment);
    }

    @Test
    @DisplayName("Get Last Test callback on null state successfully returns assessment")
    void getLastTestForNullAssessmentType_assessmentReturnsSuccessfully() {
        var assessment = AssessmentBuilderHelper.buildFullAssessmentDetails();
        var translatedAssessment = AssessmentVO.build(assessment);
        var user = new User();
        user.setSourceId(assessment.getSourceId());
        when(userSession.getUser()).thenReturn(user);

        when(assessmentService.getCurrentAssessmentIfExists(assessment.getSourceId())).thenReturn(assessment);
        var returnedAssessment = callback.getLastTest(null);
        assertThat(returnedAssessment).usingRecursiveComparison().isEqualTo(translatedAssessment);
    }

    @Test
    @DisplayName("Get Last Test callback on null assessment returns null")
    void getLastTestDoesNotExist_assessmentReturnsNull() {
        var user = new User();
        user.setSourceId(1L);
        when(userSession.getUser()).thenReturn(user);
        when(assessmentService.getAssessmentsByUserIdAndTestState(user.getSourceId(), TestState.PRACTICE))
                .thenReturn(null);
        var returnedAssessment = callback.getLastTest(TestState.PRACTICE);
        assertThat(returnedAssessment).isNull();
    }

    @Test
    @DisplayName("Get Last Test callback on no assessment returns null")
    void getLastTestDoesNotExistWithEmptyList_assessmentReturnsNull() {
        var user = new User();
        user.setSourceId(1L);
        when(userSession.getUser()).thenReturn(user);
        when(assessmentService.getAssessmentsByUserIdAndTestState(user.getSourceId(), TestState.IN_PROGRESS))
                .thenReturn(List.of());
        var returnedAssessment = callback.getLastTest(TestState.IN_PROGRESS);
        assertThat(returnedAssessment).isNull();
    }

    @Test
    @DisplayName("Get Random Question callback made.")
    void getRandomQuestionCalled_questionCallbackMade() {
        var question = AssessmentQuestionVO.build(AssessmentBuilderHelper.buildFullAssessmentQuestion());
        when(questionCallback.getRandomQuestion("pool", 1, 5))
                .thenReturn(question);
        assertThat(callback.getRandomQuestion("pool", 1, 5)).isEqualTo(question);
    }

    @Test
    @DisplayName("Get Random Question In Range callback made.")
    void getRandomQuestionInRangeCalled_questionCallbackMade() {
        var question = AssessmentQuestionVO.build(AssessmentBuilderHelper.buildFullAssessmentQuestion());
        when(questionCallback.getRandomQuestionInRange("pool", 1, 1.01))
                .thenReturn(question);
        assertThat(callback.getRandomQuestionInRange("pool", 1, 1.01)).isEqualTo(question);
    }

    @Test
    @DisplayName("Resume assessment on new assessment triggers begin callback.")
    void resumeAssessmentOnNewAssessment_beginAssessmentCallbackMade() {
        var assessment = AssessmentBuilderHelper.buildFullAssessmentDetails();
        assessment.setId(null); // New assessment lacks ID
        var translatedAssessment = AssessmentVO.build(assessment);

        callback.resume(translatedAssessment);
        verify(beginCallback, times(1)).beginAssessment(translatedAssessment);

        assertThat(assessment.getStateValues()).isNotEmpty();
        assertThat(assessment.getStateValues()).containsEntry(AssessmentStateValueConstants.NO_OF_QUESTION_ISSUED, "0");
        assertThat(assessment.getStateValues()).containsEntry(AssessmentStateValueConstants.NO_QUESTIONS_SKIPPED, "0");

    }

    @Test
    @DisplayName("Resume assessment on existing assessment triggers resume logic.")
    void resumeAssessmentOnExistingAssessment_resumeAssessmentCallbackMade() {
        var assessment = AssessmentBuilderHelper.buildFullAssessmentDetails();
        var translatedAssessment = AssessmentVO.build(assessment);
        callback.resume(translatedAssessment);
        verify(resumeCallback, times(1)).resumeAssessment(translatedAssessment);
    }

}
