package com.scholastic.srmapi.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class AssessmentException extends RuntimeException {
    private final HttpStatus status;
    private final String exceptionMessage;
}
