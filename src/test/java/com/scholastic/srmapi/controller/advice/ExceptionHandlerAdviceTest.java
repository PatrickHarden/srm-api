package com.scholastic.srmapi.controller.advice;

import com.scholastic.srmapi.exception.AssessmentException;
import com.scholastic.srmapi.exception.UserNotAuthorizedException;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExceptionHandlerAdviceTest {

    @Test
    @DisplayName("Not found exception")
    void handleException_notFound() {
        ExceptionHandlerAdvice advice = new ExceptionHandlerAdvice();
        var result = advice.handleException(
                new AssessmentException(HttpStatus.NOT_FOUND, "No srm assessment found for the student"));

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertEquals("No srm assessment found for the student", result.getBody());
    }

    @Test
    @DisplayName("Jwt exception")
    void handleJwtException_badSession() {
        ExceptionHandlerAdvice advice = new ExceptionHandlerAdvice();
        var result = advice.handleException(new JwtException("Bad session token"));

        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
        assertEquals("Bad session token", result.getBody());
    }

    @Test
    @DisplayName("Unauthorized exception")
    void handleUnauthorizedException_unauthorized() {
        ExceptionHandlerAdvice advice = new ExceptionHandlerAdvice();
        var result = advice.handleException(new UserNotAuthorizedException("Unauth"));

        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
        assertEquals("Unauth", result.getBody());
    }

    @Test
    @DisplayName("Internal server exception")
    void handleInternalException_serverException() {
        ExceptionHandlerAdvice advice = new ExceptionHandlerAdvice();
        var result = advice.handleException(new RuntimeException("Internal error"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals("Internal error", result.getBody());
    }
}
