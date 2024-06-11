package com.scholastic.srmapi.service.support;

import com.scholastic.aa.api.enums.TeacherJudgement;
import com.scholastic.aa.api.enums.TestState;
import com.scholastic.srmapi.exception.AssessmentException;
import com.scholastic.srmapi.model.AssessmentTestDetails;
import com.scholastic.srmapi.model.User;
import com.scholastic.srmapi.model.UserSession;
import com.scholastic.srmapi.repository.AssessmentTestRepository;
import com.scholastic.srmapi.response.Assessment;
import com.scholastic.srmapi.response.SrmStatus;
import com.scholastic.srmapi.service.AssessmentService;
import com.scholastic.srmapi.service.BatchAssignmentService;
import com.scholastic.srmapi.util.Constants;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class AssessmentServiceImpl implements AssessmentService {

    private static final List<String> CURRENT_ASSESSMENT_STATES = List.of(
            TestState.IN_PROGRESS.toString(), TestState.SUSPENDED.toString(), TestState.PRACTICE.toString());

    private final AssessmentTestRepository assessmentRepository;

    private final BatchAssignmentService batchAssignmentService;

    private UserSession userSession;

    @Override
    public AssessmentTestDetails getCurrentAssessment(Long sourceId) {
        var assessment = getCurrentAssessmentIfExists(sourceId);

        if (assessment == null) {
            throw new AssessmentException(HttpStatus.NOT_FOUND, Constants.NO_CURRENT_ASSESSMENTS_MESSAGE);
        }

       return assessment;
    }

    @Override
    public AssessmentTestDetails getCurrentAssessmentIfExists(Long sourceId) {
        // Check to make sure the test is still valid and not canceled by fetching the test from BAT
        SrmStatus srmDetails = getSRMDetails(sourceId);
        return getCurrentAssessment(sourceId, srmDetails);
    }

    @Override
    public void populateSessionAssessment(User user) {
        SrmStatus srmDetails = getSRMDetails(user.getSourceId());

        // Set necessary session fields from BAT
        if (srmDetails != null && !isSrmCanceled(srmDetails)) {
            if (srmDetails.getTeacherJudgement() != null) {
                userSession.setTeacherJudgement(TeacherJudgement.valueOf(srmDetails.getTeacherJudgement()));
            }
            userSession.setLexileScore(srmDetails.getLexile());
        } else {
            throw new AssessmentException(HttpStatus.NOT_FOUND, Constants.NO_CURRENT_ASSESSMENTS_MESSAGE);
        }

        // Set user and assessment to session
        userSession.setUser(user);
        userSession.setAssessmentTestDetails(getCurrentAssessment(user.getSourceId(), srmDetails));
    }

    @Override
    public void updateAssessmentSession(Long sessionId, AssessmentTestDetails assessment) {
        assessment.setUserLoginSessionId(sessionId);
        assessmentRepository.save(assessment);
    }

    @Override
    public List<AssessmentTestDetails> getAssessmentsByUserIdAndTestState(long studentId, TestState testState) {
        if (TestState.FINISHED == testState) {
            return getAssessmentsByUserIdAndTestStatesNoDateRestrictions(studentId, List.of(testState));
        } else {
            return getAssessmentsByUserIdAndTestStates(studentId, List.of(testState));
        }
    }

    @Override
    public List<AssessmentTestDetails> getAssessmentsByUserIdAndTestStates(long studentId, List<TestState> testStates) {
        return assessmentRepository.findCurrentBySourceIdAndTestStates(
                studentId,
                testStates.stream().map(Object::toString).collect(Collectors.toList()));
    }

    private AssessmentTestDetails getCurrentAssessment(Long sourceId, SrmStatus status) {
        List<AssessmentTestDetails> assessments = findAllCurrentAssessmentsBySource(sourceId);
        if (assessments == null || assessments.isEmpty()) {
            return null;
        }

        boolean srmCanceled = isSrmCanceled(status);

        if (srmCanceled
                || !isAssessmentCurrent(assessments.get(0))
                || !isAssessmentAvailable(assessments.get(0))) {
            return null;
        } else if (assessments.size() > 1) {
            log.error("Student with student id {} has {} current assessments", sourceId, assessments.size());
        }

        return assessments.get(0);
    }

    @Override
    @Transactional
    public void cancelAssessments() {
        var currentInstant = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        assessmentRepository.cancelOutdatedAssessmentsInProgress(currentInstant);
        var assessmentTestDetails = assessmentRepository
                .getRecentlyCancelledAssessments(currentInstant);

        batchAssignmentService.cancelOutdatedAssessments(assessmentTestDetails.stream()
                .map(AssessmentTestDetails::getId).collect(Collectors.toSet())).subscribe();
    }

    public List<AssessmentTestDetails> findAllCurrentAssessmentsBySource(Long sourceId) {
        return assessmentRepository.findCurrentBySourceIdAndTestStates(sourceId, CURRENT_ASSESSMENT_STATES);
    }

    private List<AssessmentTestDetails> getAssessmentsByUserIdAndTestStatesNoDateRestrictions(long studentId, List<TestState> testStates) {
        return assessmentRepository.findCurrentBySourceIdAndTestStatesNoRestrictions(
                studentId,
                testStates.stream().map(Object::toString).collect(Collectors.toList()));
    }

    private SrmStatus getSRMDetails(Long sourceId) {
        return Optional.ofNullable(batchAssignmentService.getStudentAssessment(sourceId).block()).stream()
                .filter(Objects::nonNull)
                .map(Assessment::getSrmStatus)
                .filter(Objects::nonNull)
                .findFirst().orElse(null);
    }

    private boolean isSrmCanceled(SrmStatus status) {
        return Optional.ofNullable(status).stream()
                .filter(Objects::nonNull)
                .map(SrmStatus::getStatus)
                .findFirst()
                .map(Constants.LEXILE_STATUS_CANCELED::equals)
                .orElse(false);
    }

    private boolean isAssessmentCurrent(AssessmentTestDetails assessment) {
        return Optional.of(assessment)
                .map(AssessmentTestDetails::getTestState)
                .map(Enum::toString)
                .filter(CURRENT_ASSESSMENT_STATES::contains)
                .isPresent();
    }

    private boolean isAssessmentAvailable(AssessmentTestDetails assessment) {
        return Optional.of(assessment)
                .filter(a -> a.getAssignedDate() != null)
                .filter(a -> a.getAssignedDate().compareTo(
                        Date.from(ZonedDateTime.now(ZoneId.of("UTC")).toInstant())) <= 0)
                .isPresent();
    }
}
