package com.scholastic.srmapi.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UserNotAuthorizedException extends RuntimeException {

    private static final HttpStatus status = HttpStatus.UNAUTHORIZED;

    public UserNotAuthorizedException(String message) {
        super(message);
    }

    public HttpStatus getStatus() {
        return status;
    }
}
