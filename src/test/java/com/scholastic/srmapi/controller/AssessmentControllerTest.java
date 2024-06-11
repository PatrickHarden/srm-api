package com.scholastic.srmapi.controller;

import com.scholastic.srmapi.helper.AssessmentBuilderHelper;
import com.scholastic.srmapi.helper.JwtHelper;
import com.scholastic.srmapi.model.UserSession;
import com.scholastic.srmapi.response.Assessment;
import com.scholastic.srmapi.response.AssessmentAnswerResponse;
import com.scholastic.srmapi.response.AssessmentTestResponse;
import com.scholastic.srmapi.service.support.AssessmentInterfaceServiceImpl;
import com.scholastic.srmapi.service.support.AssessmentServiceImpl;
import com.scholastic.srmapi.service.support.BatchAssignmentServiceImpl;
import io.github.benas.jpopulator.impl.PopulatorBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssessmentControllerTest {

    @Mock
    private BatchAssignmentServiceImpl batchAssignmentServiceImpl;

    @Mock
    private AssessmentServiceImpl assessmentServiceImpl;

    @Mock
    private AssessmentInterfaceServiceImpl assessmentInterfaceServiceImpl;

    @Mock
    private UserSession userSession;

    @InjectMocks
    private AssessmentController assessmentController;
    final Assessment assessment = new PopulatorBuilder().build().populateBean(Assessment.class);

    @Test
    @DisplayName("Get student lexile status assessment")
    void getAssessment() {
        when(batchAssignmentServiceImpl.getStudentAssessment(10L)).thenReturn(Mono.just(assessment));

        Mono<Assessment> responseEntity = assessmentController.getStudentAssessment(10L);
        Assessment response = responseEntity.block();
        assertNotNull(response);

        var studentLexileStatusResponse = response.getSrmStatus();
        assertNotNull(studentLexileStatusResponse);

        var lexileAssessmentResponse = assessment.getSrmStatus();
        assertEquals(lexileAssessmentResponse.getAssessmentTest(), studentLexileStatusResponse.getAssessmentTest());
        assertEquals(lexileAssessmentResponse.getId(), studentLexileStatusResponse.getId());
        assertEquals(lexileAssessmentResponse.getStudentId(), studentLexileStatusResponse.getStudentId());
        assertEquals(lexileAssessmentResponse.getTeacherId(), studentLexileStatusResponse.getTeacherId());
        assertEquals(lexileAssessmentResponse.getBenchmark(), studentLexileStatusResponse.getBenchmark());
        assertEquals(lexileAssessmentResponse.getAssignedDate(), studentLexileStatusResponse.getAssignedDate());
        assertEquals(lexileAssessmentResponse.getCompletedDate(), studentLexileStatusResponse.getCompletedDate());
        assertEquals(lexileAssessmentResponse.getLexile(), studentLexileStatusResponse.getLexile());
        assertEquals(lexileAssessmentResponse.getStatus(), studentLexileStatusResponse.getStatus());
        assertEquals(lexileAssessmentResponse.getEndDate(), studentLexileStatusResponse.getEndDate());
        assertEquals(lexileAssessmentResponse.getAdminAssignmentId(), studentLexileStatusResponse.getAdminAssignmentId());
        assertEquals(lexileAssessmentResponse.getTeacherJudgement(), studentLexileStatusResponse.getTeacherJudgement());
        assertEquals(lexileAssessmentResponse.getCancelledByUserId(), studentLexileStatusResponse.getCancelledByUserId());
        assertEquals(lexileAssessmentResponse.getModifiedDate(), studentLexileStatusResponse.getModifiedDate());
        assertEquals(lexileAssessmentResponse.getCurrentQuestion(), studentLexileStatusResponse.getCurrentQuestion());
        assertEquals(lexileAssessmentResponse.getTimeSpent(), studentLexileStatusResponse.getTimeSpent());
    }

    @Test
    @DisplayName("Get student details on fully-populated current assessment")
    void getAssessmentFullTestDetails_detailsReturnedSuccessfully() {
        var assessmentTestDetails = AssessmentBuilderHelper.buildFullAssessmentDetails();
        when(assessmentServiceImpl.getCurrentAssessment(2L)).thenReturn(assessmentTestDetails);
        var user = JwtHelper.buildDefaultValidUser();

        ResponseEntity<AssessmentTestResponse> responseEntity = assessmentController.getCurrentAssessment(
                2L, user);
        AssessmentTestResponse assessmentTestResponse = responseEntity.getBody();
        assertNotNull(assessmentTestResponse);

        // Required fields
        assertThat(assessmentTestResponse.getId()).isEqualTo(assessmentTestDetails.getId());
        assertThat(assessmentTestResponse.getStudentId()).isEqualTo(1L);
        assertThat(assessmentTestResponse.getNumberSkipsPossible()).isEqualTo(assessmentTestDetails.getNumberSkipsPossible());
        assertThat(assessmentTestResponse.getCurrentAssessmentQuestion()).isEqualTo(assessmentTestDetails.getCurrentQuestion());
        assertThat(assessmentTestResponse.getPracticeQuestionsPossible()).isEqualTo(assessmentTestDetails.getPracticeQuestionsPossible());
        assertThat(assessmentTestResponse.getLexile()).isEqualTo(assessmentTestDetails.getEndLexile());
        assertThat(assessmentTestResponse.getDateStarted()).isEqualTo(assessmentTestDetails.getCreatedDate());
        assertThat(assessmentTestResponse.getDateAssigned()).isEqualTo(assessmentTestDetails.getAssignedDate());
        assertThat(assessmentTestResponse.getLexileFormatted()).isEqualTo(assessmentTestDetails.getEndLexile() + "L");


        // Optional fields
        assertThat(assessmentTestResponse.getPracticeQuestionsTaken()).isEqualTo(assessmentTestDetails.getPracticeQuestionsTaken());
        assertThat(assessmentTestResponse.getNumberSkipsTaken()).isEqualTo(assessmentTestDetails.getNumberSkipsTaken());
        assertThat(assessmentTestResponse.getTestState()).isEqualTo(assessmentTestDetails.getTestState().toString());
        assertThat(assessmentTestResponse.getTestType()).isEqualTo(assessmentTestDetails.getTestType().toString());
        assertThat(assessmentTestResponse.getDateLastActivity()).isEqualTo(assessmentTestDetails.getModifiedDate());
    }

    @Test
    @DisplayName("Get student question on fully-populated current assessment")
    void getAssessmentFullTestDetails_questionsReturnedSuccessfully() {
        var assessmentTestDetails = AssessmentBuilderHelper.buildFullAssessmentDetails();

        assessmentTestDetails.setSourceId(2L);
        assessmentTestDetails.setUserLoginSessionId(3L);
        when(assessmentServiceImpl.getCurrentAssessment(2L)).thenReturn(assessmentTestDetails);

        ResponseEntity<AssessmentTestResponse> responseEntity = assessmentController.getCurrentAssessment(
                2L, JwtHelper.buildDefaultValidUser());
        AssessmentTestResponse assessmentTestResponse = responseEntity.getBody();
        assertThat(assessmentTestResponse).isNotNull();

        // Assessment question
        assertThat(assessmentTestResponse.getQuestion()).isNotNull();

        var issuedQuestion = assessmentTestDetails.getIssuedQuestions().stream()
                .filter(q -> assessmentTestDetails.getCurrentQuestion() == q.getQuestionSeq())
                .findFirst().get();
        assertThat(assessmentTestResponse.getQuestion().getId()).isEqualTo(issuedQuestion.getAssessmentQuestion().getId());
        assertThat(assessmentTestResponse.getQuestion().getFillInBlank()).isEqualTo(issuedQuestion.getAssessmentQuestion().getQuestion());
        assertThat(assessmentTestResponse.getQuestion().getPassage()).isEqualTo(issuedQuestion.getAssessmentQuestion().getPassage());
        assertThat(assessmentTestResponse.getQuestion().getLexile()).isEqualTo(issuedQuestion.getAssessmentQuestion().getLexile());
        assertThat(assessmentTestResponse.getQuestion().getSourceId()).isEqualTo(issuedQuestion.getAssessmentQuestion().getSourceId());

        var actualAnswers = assessmentTestResponse.getQuestion().getAnswers().stream().map(AssessmentAnswerResponse::getLabel).toList();
        var expectedAssessmentValues = List.of(issuedQuestion.getAssessmentQuestion().getAnswerCorrect(),
                issuedQuestion.getAssessmentQuestion().getAnswer2(),
                issuedQuestion.getAssessmentQuestion().getAnswer3(),
                issuedQuestion.getAssessmentQuestion().getAnswer4());

        assertThat(actualAnswers).containsExactlyInAnyOrderElementsOf(expectedAssessmentValues);
    }

    @Test
    @DisplayName("Get student details on minimally-populated current assessment")
    void getAssessmentBaseTestDetails_detailsReturnedSuccessfully() {
        var assessmentTestDetails = AssessmentBuilderHelper.buildBaseAssessmentDetails();
        assessmentTestDetails.setSourceId(2L);
        assessmentTestDetails.setUserLoginSessionId(3L);
        when(assessmentServiceImpl.getCurrentAssessment(assessmentTestDetails.getSourceId())).thenReturn(assessmentTestDetails);

        ResponseEntity<AssessmentTestResponse> responseEntity = assessmentController.getCurrentAssessment(
                2L, JwtHelper.buildDefaultValidUser());
        AssessmentTestResponse assessmentTestResponse = responseEntity.getBody();
        assertNotNull(assessmentTestResponse);

        // Required fields
        assertThat(assessmentTestResponse.getId()).isEqualTo(assessmentTestDetails.getId());
        assertThat(assessmentTestResponse.getStudentId()).isEqualTo(2L);
        assertThat(assessmentTestResponse.getNumberSkipsPossible()).isEqualTo(assessmentTestDetails.getNumberSkipsPossible());
        assertThat(assessmentTestResponse.getCurrentAssessmentQuestion()).isEqualTo(assessmentTestDetails.getCurrentQuestion());
        assertThat(assessmentTestResponse.getPracticeQuestionsPossible()).isEqualTo(assessmentTestDetails.getPracticeQuestionsPossible());
        assertThat(assessmentTestResponse.getLexile()).isEqualTo(assessmentTestDetails.getEndLexile());
        assertThat(assessmentTestResponse.getDateStarted()).isEqualTo(assessmentTestDetails.getCreatedDate());
        assertThat(assessmentTestResponse.getDateAssigned()).isEqualTo(assessmentTestDetails.getAssignedDate());
        assertThat(assessmentTestResponse.getLexileFormatted()).isEqualTo("BR" + Math.abs(assessmentTestDetails.getEndLexile()) + "L");

        // Optional fields
        assertThat(assessmentTestResponse.getPracticeQuestionsTaken()).isNull();
        assertThat(assessmentTestResponse.getNumberSkipsTaken()).isNull();
        assertThat(assessmentTestResponse.getTestState()).isEqualTo(assessmentTestDetails.getTestState().toString());
        assertThat(assessmentTestResponse.getTestType()).isNull();
        assertThat(assessmentTestResponse.getDateLastActivity()).isNull();
    }

    @Test
    @DisplayName("Get student details on filtered questions for current assessment")
    void getAssessmentBaseTestDetails_testFilteredQuestions() {
        var assessmentTestDetails = AssessmentBuilderHelper.buildBaseAssessmentDetails();
        assessmentTestDetails.setIssuedQuestions(AssessmentBuilderHelper.buildFullQuestionsList());

        assessmentTestDetails.setSourceId(2L);
        assessmentTestDetails.setUserLoginSessionId(3L);
        when(assessmentServiceImpl.getCurrentAssessment(assessmentTestDetails.getSourceId())).thenReturn(assessmentTestDetails);

        ResponseEntity<AssessmentTestResponse> responseEntity = assessmentController.getCurrentAssessment(
                2L, JwtHelper.buildDefaultValidUser());
        AssessmentTestResponse assessmentTestResponse = responseEntity.getBody();
        assertNotNull(assessmentTestResponse);

        // Required fields
        assertThat(assessmentTestResponse.getId()).isEqualTo(assessmentTestDetails.getId());
        assertThat(assessmentTestResponse.getStudentId()).isEqualTo(2L);
        assertThat(assessmentTestResponse.getNumberSkipsPossible()).isEqualTo(assessmentTestDetails.getNumberSkipsPossible());
        assertThat(assessmentTestResponse.getCurrentAssessmentQuestion()).isEqualTo(assessmentTestDetails.getCurrentQuestion());
        assertThat(assessmentTestResponse.getPracticeQuestionsPossible()).isEqualTo(assessmentTestDetails.getPracticeQuestionsPossible());
        assertThat(assessmentTestResponse.getLexile()).isEqualTo(assessmentTestDetails.getEndLexile());
        assertThat(assessmentTestResponse.getDateStarted()).isEqualTo(assessmentTestDetails.getCreatedDate());
        assertThat(assessmentTestResponse.getDateAssigned()).isEqualTo(assessmentTestDetails.getAssignedDate());
        assertThat(assessmentTestResponse.getLexileFormatted()).isEqualTo("BR" + Math.abs(assessmentTestDetails.getEndLexile()) + "L");

        // Optional fields
        assertThat(assessmentTestResponse.getPracticeQuestionsTaken()).isNull();
        assertThat(assessmentTestResponse.getNumberSkipsTaken()).isNull();
        assertThat(assessmentTestResponse.getTestState()).isEqualTo(assessmentTestDetails.getTestState().toString());
        assertThat(assessmentTestResponse.getTestType()).isNull();
        assertThat(assessmentTestResponse.getDateLastActivity()).isNull();
    }

    @Test
    @DisplayName("Start student test for assessment")
    void starAssessment_returnsSuccessfully() {
        var user = JwtHelper.buildDefaultValidUser();
        var assessmentTestDetails = AssessmentBuilderHelper.buildBaseAssessmentDetails();
        assessmentTestDetails.setIssuedQuestions(AssessmentBuilderHelper.buildFullQuestionsList());

        assessmentTestDetails.setSourceId(user.getSourceId());
        assessmentTestDetails.setUserLoginSessionId(user.getSessionId());
        doNothing().when(assessmentServiceImpl).populateSessionAssessment(user);
        doNothing().when(assessmentInterfaceServiceImpl).prepareAndStartAssessment();
        ReflectionTestUtils.setField(assessmentController, "session", userSession);
        when(userSession.getAssessmentTestDetails()).thenReturn(assessmentTestDetails);

        ResponseEntity<AssessmentTestResponse> responseEntity = assessmentController.beginAssessment(
                user.getSourceId(), user);
        AssessmentTestResponse assessmentTestResponse = responseEntity.getBody();
        assertNotNull(assessmentTestResponse);

        assertThat(assessmentTestResponse).usingRecursiveComparison().isEqualTo(AssessmentTestResponse.buildAssessmentResponse(assessmentTestDetails));
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }
}
