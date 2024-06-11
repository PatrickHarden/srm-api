package com.scholastic.srmapi.controller.advice;

import com.scholastic.srmapi.exception.AssessmentException;
import com.scholastic.srmapi.exception.UserNotAuthorizedException;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;


@ControllerAdvice
public class ExceptionHandlerAdvice {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandlerAdvice.class);

    @ExceptionHandler(AssessmentException.class)
    public ResponseEntity<String> handleException(AssessmentException e) {
        LOGGER.error(e.getExceptionMessage());
        return ResponseEntity.status(e.getStatus()).body(e.getExceptionMessage());
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<String> handleException(JwtException e) {
        LOGGER.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }

    @ExceptionHandler( UserNotAuthorizedException.class)
    public ResponseEntity<String> handleException(UserNotAuthorizedException e) {
        LOGGER.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({ RuntimeException.class })
    public ResponseEntity<String> handleException(RuntimeException e) {
        LOGGER.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());    }

}
