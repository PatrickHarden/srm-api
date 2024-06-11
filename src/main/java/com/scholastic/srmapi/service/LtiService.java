package com.scholastic.srmapi.service;

import javax.servlet.http.HttpServletRequest;

public interface LtiService {
    /**
     * Validates a LTI request
     * 
     * @param request the {@code HttpServletRequest} to validate against
     * @return {@code true} if LTI request is valid {@code false} otherwise
     */
    boolean validateLtiRequest(HttpServletRequest request);

    /**
     * Creates a new user login session and returns the user session ID.
     * @param sourceId source ID of the session
     * @return Long ID of the user login session
     */
    Long createUserSession(String sourceId);
}
