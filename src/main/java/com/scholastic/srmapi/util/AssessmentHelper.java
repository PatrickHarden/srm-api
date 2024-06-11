package com.scholastic.srmapi.util;

import com.scholastic.srmapi.model.AssessmentIssuedQuestion;
import com.scholastic.srmapi.model.AssessmentTestDetails;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;

/**
 * Static Helper for common assessment methods.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AssessmentHelper {

    /**
     * Retrieves the currently assigned question for the provided {@code AssessmentTestDetails} test.
     * @param assessmentTestDetails The {@code AssessmentTestDetails} for a provided assessment.
     * @return The currently assigned question.
     */
    public static AssessmentIssuedQuestion getCurrentQuestionForAssessment(AssessmentTestDetails assessmentTestDetails) {
        int currentQuestion = assessmentTestDetails.getCurrentQuestion();
        return Optional.of(assessmentTestDetails).stream()
                .filter(test -> test.getCurrentQuestion() >= 1)
                .filter(test -> test.getIssuedQuestions() != null)
                .flatMap(test -> test.getIssuedQuestions().stream())
                .filter(question -> currentQuestion == question.getQuestionSeq())
                .findFirst().orElse(null);
    }

    /**
     * Converts a given {@code int} into its equivalent {@code String} Lexile format.
     * @param lexile The {@code int} to convert to Lexile format.
     * @return The properly-formatted Lexile.
     */
    public static String intToLexile(int lexile) {
        String formattedLexile;
        if (lexile >= 0) {
            formattedLexile = lexile + "L";
        } else {
            formattedLexile = "BR" + Math.abs(lexile) + "L";
        }
        return formattedLexile;
    }

    /**
     * Gets current date time
     * @return current date
     */
    public static Date getCurrentDateTime() {
        return Date.from(ZonedDateTime.now(ZoneId.of("UTC")).toInstant());
    }
}
