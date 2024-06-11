package com.scholastic.srmapi.assessment.callback;

import com.scholastic.aa.api.IAssessment;
import com.scholastic.aa.api.IAssessmentCallback;
import com.scholastic.aa.api.IAssessmentQuestion;
import com.scholastic.aa.api.enums.Grade;
import com.scholastic.aa.api.enums.SchoolYearPeriod;
import com.scholastic.aa.api.enums.TeacherJudgement;
import com.scholastic.aa.api.enums.TestState;
import com.scholastic.aa.api.util.AssessmentStateValueConstants;
import com.scholastic.srmapi.model.AssessmentTestDetails;
import com.scholastic.srmapi.model.UserSession;
import com.scholastic.srmapi.model.vo.AssessmentVO;
import com.scholastic.srmapi.service.AssessmentService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequestScope
@AllArgsConstructor
public class LitproUSAssessmentCallback implements IAssessmentCallback {

    private AssessmentBeginCallback beginCallback;

    private AssessmentService assessmentService;

    private UserSession userSession;

    private AssessmentQuestionCallback questionCallback;

    private AssessmentResumeCallback resumeCallback;

    @Override
    public void finish(BigDecimal finalMeasure) {
        log.debug("Finishing test with finalMeasure {}", finalMeasure);
    }

    @Override
    public int getDefaultMeasure(Grade grade, TeacherJudgement judgement, SchoolYearPeriod period) {
        log.debug("Default measure of grade {} with teacher judgement {} and SchoolYearPeriod {}",
                grade,
                judgement.getCode(),
                period.getCode());
        return 0;
    }
    @Override
    public IAssessment getLastTest(TestState testState) {
        var student = userSession.getUser();
        log.warn("Searching for test state {} for user ID {}", testState, student.getSourceId());

        // If testState is null, assume that we want the most recent current test
        if (testState == null) {
            AssessmentTestDetails assessment = assessmentService.getCurrentAssessmentIfExists(student.getSourceId());
            return AssessmentVO.build(assessment);
        }

        List<AssessmentTestDetails> assessments = assessmentService.getAssessmentsByUserIdAndTestState(student.getSourceId(), testState);
        if (assessments == null || assessments.isEmpty()) {
            return null;
        } else {
            return AssessmentVO.build(assessments.get(0));
        }
    }

    @Override
    public IAssessmentQuestion getQuestion(String arg0) {
        return null;
    }

    @Override
    public IAssessmentQuestion getRandomQuestion(String pool, int scoreLow, int scoreHigh) {
        log.warn("Get Random Question called with pool of {}, low score of {}, and high score of {}",
                pool, scoreLow, scoreHigh);
        return questionCallback.getRandomQuestion(pool, scoreLow, scoreHigh);
    }

    @Override
    public IAssessmentQuestion getRandomQuestionInRange(String pool, int range, double score) {
        log.warn("Get Random Question In Range called with pool of {}, range of {}, and score of {}",
                pool, range, score);
        return questionCallback.getRandomQuestionInRange(pool, range, score);
    }

    @Override
    public void presentQuestion(IAssessmentQuestion assessmentQuestion) {
        log.warn("Present Question called (to save new assessment Q to user) with question {}", assessmentQuestion.getQuestion());
    }

    @Override
    public void resume(IAssessment assessment) {
        log.warn("Resuming assessment called");
        if (assessment.getId() != null) {
            log.warn("Resuming existing assessment with id {}", assessment.getId());

            // Added for demoable purposes, will be removed with callback refactoring.
            if (TestState.SUSPENDED.equals(assessment.getState())) {
                userSession.setAssessmentTestDetails(assessmentService.getAssessmentsByUserIdAndTestState(
                        userSession.getUser().getSourceId(), TestState.SUSPENDED).get(0) );
            }
            resumeCallback.resumeAssessment(assessment);
        } else {
            assessment.getStateValues().put(AssessmentStateValueConstants.NO_OF_QUESTION_ISSUED, "0");
            assessment.getStateValues().put(AssessmentStateValueConstants.NO_QUESTIONS_SKIPPED, "0");
            beginCallback.beginAssessment(assessment);
        }
    }

    @Override
    public void saveState(TestState testState, Map<String, String> stateValueMap) {
        log.warn("Save state was called with test state {} and state value map of {}",
                testState, stateValueMap.size());
    }

    @Override
    public void skip() {
        log.warn("Skip called");
    }

}
