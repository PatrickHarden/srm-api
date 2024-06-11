package com.scholastic.srmapi.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * Represents a single assessment question for the current assessment to be returned to the user.
 */
@Getter
@Builder
public class AssessmentQuestionResponse {
    private Integer id;
    private String passage;
    private String fillInBlank;
    private List<AssessmentAnswerResponse> answers;
    private String sourceId;
    private double lexile;
}
