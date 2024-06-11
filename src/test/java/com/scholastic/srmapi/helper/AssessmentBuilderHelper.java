package com.scholastic.srmapi.helper;

import com.scholastic.aa.api.enums.TeacherJudgement;
import com.scholastic.aa.api.enums.TestState;
import com.scholastic.aa.api.enums.TestType;
import com.scholastic.srmapi.model.AssessmentIssuedQuestion;
import com.scholastic.srmapi.model.AssessmentQuestion;
import com.scholastic.srmapi.model.AssessmentTestDetails;
import com.scholastic.srmapi.response.Assessment;
import com.scholastic.srmapi.response.SrmStatus;


import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class AssessmentBuilderHelper {

    private static final Date baseAssignedDate = Date.from(Instant.now());
    private static final Date baseModifiedDate = Date.from(Instant.now());
    private static final Date baseAnsweredTime = Date.from(Instant.now());

    public static Assessment buildAssessment(boolean canceled) {
        var assessment = new Assessment();
        var srm = new SrmStatus();
        srm.setStatus(canceled ? "CANCELED" : "ASSIGNED");
        srm.setTeacherJudgement(TeacherJudgement.ON_LEVEL.toString());
        srm.setLexile(BigDecimal.valueOf(1.1));
        assessment.setSrmStatus(srm);
        return assessment;
    }
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
        testDetails.setStateValues(Map.of("key1", "val1", "key2", "val2"));
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

    public static List<AssessmentIssuedQuestion> buildFullQuestionsList() {
        var aq1 = buildIssuedQuestion();
        aq1.setQuestionSeq(1);
        aq1.setCorrectAnsDisplay(1);
        var aq2 = buildIssuedQuestion();
        aq2.setQuestionSeq(2);
        aq2.setCorrectAnsDisplay(2);
        var aq3 = buildIssuedQuestion();
        aq3.setQuestionSeq(3);
        aq3.setCorrectAnsDisplay(3);
        var aq4 = buildIssuedQuestion();
        aq4.setQuestionSeq(4);
        aq4.setCorrectAnsDisplay(4);

        return List.of(aq1, aq2, aq3, aq4);
    }
}
