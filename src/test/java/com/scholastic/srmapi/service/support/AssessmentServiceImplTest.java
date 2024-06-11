package com.scholastic.srmapi.service.support;

import com.scholastic.aa.api.enums.TeacherJudgement;
import com.scholastic.aa.api.enums.TestState;
import com.scholastic.srmapi.config.BaWebClientConfig;
import com.scholastic.srmapi.exception.AssessmentException;
import com.scholastic.srmapi.helper.AssessmentBuilderHelper;
import com.scholastic.srmapi.helper.JwtHelper;
import com.scholastic.srmapi.model.User;
import com.scholastic.srmapi.model.UserSession;
import com.scholastic.srmapi.repository.AssessmentTestRepository;
import com.scholastic.srmapi.response.Assessment;
import com.scholastic.srmapi.service.AssessmentService;
import com.scholastic.srmapi.util.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;

import java.sql.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@SpringBootTest(classes = { AssessmentService.class, BaWebClientConfig.class })
class AssessmentServiceImplTest {

    @Autowired
    public BaWebClientConfig config;

    AssessmentService assessmentService;

    @Mock
    BatchAssignmentServiceImpl batchAssignmentService;

    @Mock
    AssessmentTestRepository assessmentTestRepository;

    @Mock
    UserSession userSession;

    final Assessment batchAssessmentDetails = AssessmentBuilderHelper.buildAssessment(false);

    final User user = JwtHelper.buildValidUser(1L, 2L);

    @BeforeEach
    void setup() {
        assessmentService = new AssessmentServiceImpl(assessmentTestRepository, batchAssignmentService, userSession);
    }

    @Test
    @DisplayName("Get current assessment with null assigned throws exception")
    void getCurrentAssignedAssessmentWithNoneAssigned_validateNoAssessmentsThrown() {
        when(assessmentTestRepository.findCurrentBySourceIdAndTestStates(anyLong(), anyList()))
                .thenReturn(null);

        when(batchAssignmentService.getStudentAssessment(1L)).thenReturn(Mono.just(batchAssessmentDetails));

        AssessmentException ae = assertThrows(AssessmentException.class,
                () -> assessmentService.getCurrentAssessment(1L),
                Constants.NO_CURRENT_ASSESSMENTS_MESSAGE);

        assertEquals(HttpStatus.NOT_FOUND, ae.getStatus());
        assertEquals(Constants.NO_CURRENT_ASSESSMENTS_MESSAGE, ae.getExceptionMessage());

    }

    @Test
    @DisplayName("Get current assessment with empty assigned throws exception")
    void getCurrentAssignedAssessmentWithEmptyAssigned_validateNoAssessmentsThrown() {

        when(assessmentTestRepository.findCurrentBySourceIdAndTestStates(anyLong(), anyList()))
                .thenReturn(List.of());

        when(batchAssignmentService.getStudentAssessment(1L)).thenReturn(Mono.just(batchAssessmentDetails));

        AssessmentException ae = assertThrows(AssessmentException.class,
                () -> assessmentService.getCurrentAssessment(1L),
                Constants.NO_CURRENT_ASSESSMENTS_MESSAGE);

        assertEquals(HttpStatus.NOT_FOUND, ae.getStatus());
        assertEquals(Constants.NO_CURRENT_ASSESSMENTS_MESSAGE, ae.getExceptionMessage());

    }

    @Test
    @DisplayName("Get current assessment with canceled status assigned throws exception")
    void getCurrentAssessmentCanceled_validateNoAssessmentsThrown() {
        var currAssess = AssessmentBuilderHelper.buildAssessment(true);
        var fullAssessment = AssessmentBuilderHelper.buildFullAssessmentDetails();

        when(assessmentTestRepository.findCurrentBySourceIdAndTestStates(anyLong(), anyList()))
                .thenReturn(List.of(fullAssessment));

        when(batchAssignmentService.getStudentAssessment(1L)).thenReturn(Mono.just(currAssess));

        AssessmentException ae = assertThrows(AssessmentException.class,
                () -> assessmentService.getCurrentAssessment(1L),
                Constants.NO_CURRENT_ASSESSMENTS_MESSAGE);

        assertEquals(HttpStatus.NOT_FOUND, ae.getStatus());
        assertEquals(Constants.NO_CURRENT_ASSESSMENTS_MESSAGE, ae.getExceptionMessage());

    }

    @Test
    @DisplayName("Get current assessment with future date assigned throws exception")
    void getCurrentAssessmentAssignedFuture_validateNoAssessmentsThrown() {
        var fullAssessment = AssessmentBuilderHelper.buildFullAssessmentDetails();
        fullAssessment.setAssignedDate(Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));

        when(assessmentTestRepository.findCurrentBySourceIdAndTestStates(anyLong(), anyList()))
                .thenReturn(List.of(fullAssessment));

        when(batchAssignmentService.getStudentAssessment(1L)).thenReturn(Mono.just(batchAssessmentDetails));

        AssessmentException ae = assertThrows(AssessmentException.class,
                () -> assessmentService.getCurrentAssessment(1L),
                Constants.NO_CURRENT_ASSESSMENTS_MESSAGE);

        assertEquals(HttpStatus.NOT_FOUND, ae.getStatus());
        assertEquals(Constants.NO_CURRENT_ASSESSMENTS_MESSAGE, ae.getExceptionMessage());

    }

    @Test
    @DisplayName("Get current assessment with invalid state assigned throws exception")
    void getCurrentAssessmentInBadState_validateNoAssessmentsThrown() {
        var fullAssessment = AssessmentBuilderHelper.buildFullAssessmentDetails();
        fullAssessment.setTestState(TestState.FINISHED);

        when(assessmentTestRepository.findCurrentBySourceIdAndTestStates(anyLong(), anyList()))
                .thenReturn(List.of(fullAssessment));

        when(batchAssignmentService.getStudentAssessment(1L)).thenReturn(Mono.just(batchAssessmentDetails));

        AssessmentException ae = assertThrows(AssessmentException.class,
                () -> assessmentService.getCurrentAssessment(1L),
                Constants.NO_CURRENT_ASSESSMENTS_MESSAGE);

        assertEquals(HttpStatus.NOT_FOUND, ae.getStatus());
        assertEquals(Constants.NO_CURRENT_ASSESSMENTS_MESSAGE, ae.getExceptionMessage());

    }

    @Test
    @DisplayName("Get current assessment with valid assessment assigned returns details")
    void getCurrentAssignedAssessment_validateSuccessfulRetrievalForOneAssessment() {
        var fullAssessment = AssessmentBuilderHelper.buildFullAssessmentDetails();

        when(assessmentTestRepository.findCurrentBySourceIdAndTestStates(anyLong(), anyList()))
                .thenReturn(List.of(fullAssessment));

        when(batchAssignmentService.getStudentAssessment(1L)).thenReturn(Mono.just(batchAssessmentDetails));

        var currentAssessment = assessmentService.getCurrentAssessment(1L);

        assertThat(currentAssessment).isEqualTo(fullAssessment);
    }

    @Test
    @DisplayName("Get current assessment with multiple valid assessments assigned returns details")
    void getCurrentAssignedAssessment_validateSuccessfulRetrievalForOneOfTwoAssessment() {
        var fullAssessment1 = AssessmentBuilderHelper.buildFullAssessmentDetails();
        var fullAssessment2 = AssessmentBuilderHelper.buildFullAssessmentDetails();
        fullAssessment2.setCreatedDate(new java.util.Date());

        when(assessmentTestRepository.findCurrentBySourceIdAndTestStates(anyLong(), anyList()))
                .thenReturn(List.of(fullAssessment1, fullAssessment2));

        when(batchAssignmentService.getStudentAssessment(1L)).thenReturn(Mono.just(batchAssessmentDetails));

        var currentAssessment = assessmentService.getCurrentAssessment(1L);

        assertThat(currentAssessment).isEqualTo(fullAssessment1);
    }

    @Test
    @DisplayName("Setting a new session ID for an assessment will successfully update")
    void updateAssessmentSession_validateAssessmentSessionIdUpdated() {
        var beforeAssessment = AssessmentBuilderHelper.buildFullAssessmentDetails();

        when(assessmentTestRepository.save(beforeAssessment)).thenReturn(null);

        assessmentService.updateAssessmentSession(6L, beforeAssessment);

        verify(assessmentTestRepository, times(1)).save(beforeAssessment);
        assertThat(beforeAssessment.getUserLoginSessionId()).isEqualTo(6L);
    }

    @Test
    @DisplayName("Populating session with valid SRM details sets user session details.")
    void populateSessionDetails_validateSessionCorrectlyUpdated() {
        var user = JwtHelper.buildValidUser(1L, 2L);
        var fullAssessment = AssessmentBuilderHelper.buildFullAssessmentDetails();

        when(assessmentTestRepository.findCurrentBySourceIdAndTestStates(anyLong(), anyList()))
                .thenReturn(List.of(fullAssessment));
        when(batchAssignmentService.getStudentAssessment(1L)).thenReturn(Mono.just(batchAssessmentDetails));

        assessmentService.populateSessionAssessment(user);

        verify(userSession, times(1))
                .setTeacherJudgement(TeacherJudgement.valueOf(batchAssessmentDetails.getSrmStatus().getTeacherJudgement()));
        verify(userSession, times(1)).setLexileScore(batchAssessmentDetails.getSrmStatus().getLexile());
        verify(userSession, times(1)).setUser(user);
        verify(userSession, times(1)).setAssessmentTestDetails(fullAssessment);

    }

    @Test
    @DisplayName("Populating session with bad BAT response throws error.")
    void populateSessionDetailsForUnavailableAssessment_validateExceptionThrown() {
        var badSrm = new Assessment();

        when(batchAssignmentService.getStudentAssessment(1L)).thenReturn(Mono.just(badSrm));

        AssessmentException ae = assertThrows(AssessmentException.class,
                () -> assessmentService.populateSessionAssessment(user),
                Constants.NO_CURRENT_ASSESSMENTS_MESSAGE);

        assertEquals(HttpStatus.NOT_FOUND, ae.getStatus());
        assertEquals(Constants.NO_CURRENT_ASSESSMENTS_MESSAGE, ae.getExceptionMessage());
    }

    @Test
    @DisplayName("Populating session with canceled BAT response throws error.")
    void populateSessionDetailsForCanceledAssessment_validateExceptionThrown() {
        var canceledAssessment = AssessmentBuilderHelper.buildAssessment(true);

        when(batchAssignmentService.getStudentAssessment(1L)).thenReturn(Mono.just(canceledAssessment));

        AssessmentException ae = assertThrows(AssessmentException.class,
                () -> assessmentService.populateSessionAssessment(user),
                Constants.NO_CURRENT_ASSESSMENTS_MESSAGE);

        assertEquals(HttpStatus.NOT_FOUND, ae.getStatus());
        assertEquals(Constants.NO_CURRENT_ASSESSMENTS_MESSAGE, ae.getExceptionMessage());
    }

    @Test
    @DisplayName("Get assessment by user ID and test state calls repository")
    void getAssessmentDetailsByUserIdAndState_repositoryCalled() {
        var fullAssessment = AssessmentBuilderHelper.buildFullAssessmentDetails();

        when(assessmentTestRepository.findCurrentBySourceIdAndTestStates(anyLong(), anyList()))
                .thenReturn(List.of(fullAssessment));
        var result = assessmentService.getAssessmentsByUserIdAndTestState(fullAssessment.getSourceId(), fullAssessment.getTestState());

        assertThat(result).usingRecursiveComparison().isEqualTo(List.of(fullAssessment));
    }

    @Test
    @DisplayName("Get finished assessment by user ID and test state calls non-date restricted repo call.")
    void getFinishedAssessmentDetailsByUserIdAndState_repositoryCalled() {
        var fullAssessment = AssessmentBuilderHelper.buildFullAssessmentDetails();

        when(assessmentTestRepository.findCurrentBySourceIdAndTestStatesNoRestrictions(anyLong(), anyList()))
                .thenReturn(List.of(fullAssessment));
        var result = assessmentService.getAssessmentsByUserIdAndTestState(fullAssessment.getSourceId(), TestState.FINISHED);

        assertThat(result).usingRecursiveComparison().isEqualTo(List.of(fullAssessment));
    }

    @Test
    @DisplayName("Cancel outdated assessments")
    void cancelAssessments() {
        var beforeAssessment = AssessmentBuilderHelper.buildFullAssessmentDetails();

        when(assessmentTestRepository
                .getRecentlyCancelledAssessments(any())).thenReturn(Arrays.asList(beforeAssessment));
        when(batchAssignmentService
                .cancelOutdatedAssessments(any())).thenReturn(Mono.empty());

        assessmentService.cancelAssessments();

        verify(assessmentTestRepository, times(1))
                .cancelOutdatedAssessmentsInProgress(any());
        verify(assessmentTestRepository, times(1))
                .getRecentlyCancelledAssessments(any());
        verify(batchAssignmentService, times(1))
                .cancelOutdatedAssessments(any());
    }
}
