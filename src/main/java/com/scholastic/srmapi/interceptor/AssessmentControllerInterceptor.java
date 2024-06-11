package com.scholastic.srmapi.interceptor;

import com.scholastic.srmapi.exception.UserNotAuthorizedException;
import com.scholastic.srmapi.util.Constants;
import com.scholastic.srmapi.util.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Optional;

@Component
public class AssessmentControllerInterceptor implements HandlerInterceptor {
    public Boolean validateUserDetails(HttpServletRequest request) {
        String studentId = Optional.of(request).stream()
                .map(req -> request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE))
                .filter(Map.class::isInstance)
                .map(Map.class::cast)
                .filter(m -> m.size() > 0)
                .map(m -> m.get("studentId"))
                .map(Object::toString)
                .filter(JwtTokenProvider::validateIdIsValid) // Verify that this string is a Long
            .findFirst()
            .orElseThrow(() -> new UserNotAuthorizedException(Constants.NOT_AUTHORIZED_MESSAGE));

        var claims = Optional.of(request)
                .map(req -> req.getAttribute(Constants.CLAIMS))
                .filter(Claims.class::isInstance)
                .map(Claims.class::cast)
                .filter(c -> JwtTokenProvider.deriveSessionIdFromClaims(c) != null)
                .orElseThrow(() -> new UserNotAuthorizedException(Constants.NOT_AUTHORIZED_MESSAGE));

        // Verify source ID and student ID in parameters match
        if (!JwtTokenProvider.validateClaimsMatchStudentId(claims, studentId)) {
            throw new UserNotAuthorizedException(Constants.NOT_AUTHORIZED_MESSAGE);
        }

        // Verify there is a session ID that is a number
        if (!JwtTokenProvider.validateIdIsValid(studentId)) {
            throw new UserNotAuthorizedException(Constants.NOT_AUTHORIZED_MESSAGE);
        }

        return true;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        return validateUserDetails(request);
    }

}
