package com.scholastic.srmapi.assessment.callback;

import com.scholastic.aa.api.enums.TestState;
import com.scholastic.aa.api.enums.TestType;
import com.scholastic.srmapi.model.AssessmentIssuedQuestion;
import com.scholastic.srmapi.model.AssessmentQuestion;
import com.scholastic.srmapi.model.AssessmentTestDetails;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Test class to create a dummy assessment. This will be removed once the assessment is actually saved to the DB.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AssessmentBuilderHelper {

    private static final Date baseAssignedDate = Date.from(Instant.now());
    private static final Date baseModifiedDate = Date.from(Instant.now());
    private static final Date baseAnsweredTime = Date.from(Instant.now());

    public static AssessmentTestDetails buildBaseAssessmentDetails() {
        var testDetails = new AssessmentTestDetails();
        testDetails.setId(1L);
        testDetails.setCurrentQuestion(1);
        testDetails.setStartLexile(100);
        testDetails.setStartSigma(BigDecimal.valueOf(10.000001));
        testDetails.setEndLexile(-100);
        testDetails.setEndSigma(BigDecimal.valueOf(10.000001));
        testDetails.setLexile(10.000001);
        testDetails.setNumberSkipsPossible(1);
        testDetails.setPracticeQuestionsPossible(1);
        testDetails.setTestState(TestState.FINISHED);
        return testDetails;
    }

    public static AssessmentTestDetails buildFullAssessmentDetails() {
        var testDetails = buildBaseAssessmentDetails();
        testDetails.setAssignedDate(baseAssignedDate);
        testDetails.setModifiedDate(baseModifiedDate);
        testDetails.setDescription("Some desc");
        testDetails.setNumberSkipsTaken(1);
        testDetails.setPracticeQuestionsTaken(1);
        testDetails.setSchoolYear(2022);
        testDetails.setSourceId(1L);
        testDetails.setTestState(TestState.IN_PROGRESS);
        testDetails.setTestType(TestType.INTERNAL);
        testDetails.setUncertainity(BigDecimal.valueOf(10.000001));
        testDetails.setUserLoginSessionId(1L);
        testDetails.setIssuedQuestions(Arrays.asList(buildIssuedQuestion(), buildIssuedQuestion()));
        var states = new HashMap<>(Map.of("key1", "val1", "key2", "val2"));
        testDetails.setStateValues(states);
        testDetails.setEndLexile(100);
        return testDetails;
    }

    public static AssessmentQuestion buildFullAssessmentQuestion() {
        var question = new AssessmentQuestion();
        question.setId(1);
        question.setAnswer2("ans2");
        question.setAnswer3("ans3");
        question.setAnswer4("ans4");
        question.setAnswerCorrect("ansc");
        question.setLexile(12.000001);
        question.setPassage("passage");
        question.setPool("pool");

        //Optional fields
        question.setSourceId("sourceId");
        question.setCredit("credit");
        question.setActive(false);
        question.setSuppressed(true);

        return question;
    }

    public static AssessmentIssuedQuestion buildIssuedQuestion() {
        var issuedQ = new AssessmentIssuedQuestion();
        issuedQ.setId(1);
        issuedQ.setAnswerType(1);
        issuedQ.setAnsweredTime(baseAnsweredTime);
        issuedQ.setCorrectAnsDisplay(1);
        issuedQ.setCorrectAnsIndex(1);
        issuedQ.setEndLexile(100);
        issuedQ.setEndSigma(BigDecimal.valueOf(10.000001));
        issuedQ.setQuestionLexile(1);
        issuedQ.setQuestionSeq(1);
        issuedQ.setQuestionType(1);
        issuedQ.setSkipped(true);
        issuedQ.setStartLexile(100);
        issuedQ.setStartSigma(BigDecimal.valueOf(10.000001));
        issuedQ.setStudentAnsDisplay(1);
        issuedQ.setStudentAnsIndex(1);
        issuedQ.setSuccess(true);
        issuedQ.setTimeElapsedSeconds(1);
        issuedQ.setAssessmentQuestion(buildFullAssessmentQuestion());
        return issuedQ;
    }

}
