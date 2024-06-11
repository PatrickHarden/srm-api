package com.scholastic.srmapi.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.scholastic.aa.api.enums.TestState;
import com.scholastic.srmapi.config.EnvironmentConfig;
import com.scholastic.srmapi.model.AssessmentQuestion;
import com.scholastic.srmapi.model.AssessmentTestDetails;
import com.scholastic.srmapi.util.AssessmentHelper;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.scholastic.srmapi.util.Constants.SHUFFLE_ASSESSMENT_ANSWERS;
import static com.scholastic.srmapi.util.Constants.ZERO;
import static com.scholastic.srmapi.util.Constants.ONE;
import static com.scholastic.srmapi.util.Constants.TWO;
import static com.scholastic.srmapi.util.Constants.THREE;

/**
 * Represents the single, most current SRM assessment for the current user.
 */
@Slf4j
@Getter
@Builder
public class AssessmentTestResponse {
    private Long id;
    private Long studentId;
    private String testState;
    private String testType;
    private Integer numberSkipsTaken;
    private int numberSkipsPossible;
    private Integer practiceQuestionsTaken;
    private int practiceQuestionsPossible;
    private int currentAssessmentQuestion;
    private int lexile;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:00:000Z", timezone = "America/New_York")
    private Date dateAssigned;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:00:000Z", timezone = "America/New_York")
    private Date dateStarted;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:00:000Z", timezone = "America/New_York")
    private Date dateLastActivity;
    private AssessmentQuestionResponse question;

    /**
     * Returns the current assessment Lexile, formatted as nL or BRnL, where 'n' is a positive integer.
     * @return The {@code String} representation of the current assessment Lexile.
     */
    public String getLexileFormatted() {
        return AssessmentHelper.intToLexile(this.getLexile());
    }

    /**
     * Builds an {@code AssessmentTestResponse} which will contain the status and question of a current test.
     * @param assessment The student test details represented as the model {@code AssessmentTestDetails}
     * @return A populated {@code AssessmentTestResponse} which reflects the student's test details.
     */
    public static AssessmentTestResponse buildAssessmentResponse(AssessmentTestDetails assessment) {
        AssessmentTestResponse.AssessmentTestResponseBuilder builder = AssessmentTestResponse.builder();

        builder.id(assessment.getId())
                .studentId(assessment.getSourceId())
                .numberSkipsPossible(assessment.getNumberSkipsPossible())
                .currentAssessmentQuestion(assessment.getCurrentQuestion())
                .practiceQuestionsPossible(assessment.getPracticeQuestionsPossible())
                .lexile(assessment.getEndLexile())
                .dateStarted(assessment.getCreatedDate());


        // Optional fields
        if (assessment.getAssignedDate() != null) {
            builder.dateAssigned(assessment.getAssignedDate());
        }

        if (assessment.getPracticeQuestionsTaken() != null) {
            builder.practiceQuestionsTaken(assessment.getPracticeQuestionsTaken());
        }

        if (assessment.getNumberSkipsTaken() != null) {
            builder.numberSkipsTaken(assessment.getNumberSkipsTaken());
        }

        if (assessment.getTestState() != null) {
            builder.testState(assessment.getTestState().name());
        }

        if (assessment.getTestType() != null) {
            builder.testType(assessment.getTestType().name());
        }

        if (assessment.getModifiedDate() != null) {
            builder.dateLastActivity(assessment.getModifiedDate());
        }

        // If the assessment is not finished, build question
        if (!TestState.FINISHED.equals(assessment.getTestState())) {

            // Fetch question logic
            var currentQuestion = AssessmentHelper.getCurrentQuestionForAssessment(assessment);
            var assessmentQuestion = currentQuestion.getAssessmentQuestion();
            int actualAnswerDisplay = currentQuestion.getCorrectAnsDisplay();
            builder.question(buildAssessmentQuestionResponse(assessmentQuestion, actualAnswerDisplay));
        }

        return builder.build();
    }

    private static AssessmentQuestionResponse buildAssessmentQuestionResponse(AssessmentQuestion aq, int answerDisplayIndex) {
        AssessmentQuestionResponse.AssessmentQuestionResponseBuilder aqrBuilder = AssessmentQuestionResponse.builder();
        aqrBuilder.id(aq.getId());
        aqrBuilder.fillInBlank(aq.getQuestion());
        aqrBuilder.passage(aq.getPassage());
        aqrBuilder.sourceId(aq.getSourceId());
        aqrBuilder.lexile(aq.getLexile());

        List<AssessmentAnswerResponse> answerList = Arrays.asList(
                new AssessmentAnswerResponse(answerDisplayIndex, aq.getAnswerCorrect()),
                new AssessmentAnswerResponse(answerDisplayIndex == ONE ? ZERO : ONE, aq.getAnswer2()),
                new AssessmentAnswerResponse(answerDisplayIndex == TWO ? ZERO : TWO, aq.getAnswer3()),
                new AssessmentAnswerResponse(answerDisplayIndex == THREE ? ZERO : THREE, aq.getAnswer4())
        );

        // Pull system properties to verify if the assessment has been properly shuffled.
        String shuffleAssessmentAnswers = EnvironmentConfig.getSystemVar(SHUFFLE_ASSESSMENT_ANSWERS);
        if (!"false".equalsIgnoreCase(shuffleAssessmentAnswers)) {
            // Shuffle (default behavior)
            Collections.shuffle(answerList);
        } else {
            log.debug("shuffleAssessmentAnswers is set to false, not randomizing questions");
        }
        aqrBuilder.answers(answerList);
        return aqrBuilder.build();
    }

}
