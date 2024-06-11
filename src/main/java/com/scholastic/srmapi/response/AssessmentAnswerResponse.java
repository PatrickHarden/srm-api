package com.scholastic.srmapi.response;

import lombok.Builder;
import lombok.Getter;

/**
 * Represents a single Answer in an assessment response object.
 */
@Getter
@Builder
public class AssessmentAnswerResponse {
    Integer id;
    String label;
}
