package com.scholastic.srmapi.interceptor;

import com.scholastic.srmapi.exception.UserNotAuthorizedException;
import com.scholastic.srmapi.helper.JwtHelper;
import com.scholastic.srmapi.util.Constants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.HandlerMapping;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AssessmentControllerInterceptorTest {

    AssessmentControllerInterceptor interceptor = new AssessmentControllerInterceptor();

    @Test
    @DisplayName("Pre-handle valid Assessment Controller request")
    void testSuccessfulInterceptor() {
        var req = mockValidServletRequest();
        assertThat(interceptor.preHandle(req, new MockHttpServletResponse(), "anything")).isTrue();
    }

    @Test
    @DisplayName("Throw exception when request to controller lacks Claims.")
    void interceptRequestWithoutClaims_notAuthorizedExceptionThrown() {
        var req = mockValidServletRequest();
        req.setAttribute(Constants.CLAIMS, null);
        UserNotAuthorizedException ae = assertThrows(UserNotAuthorizedException.class,
                () -> interceptor.validateUserDetails(req),
                Constants.NOT_AUTHORIZED_MESSAGE);

        assertThat(ae).isNotNull();
        assertThat(ae.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("Throw exception when request to controller lacks student ID in path.")
    void interceptRequestWithoutStudentIdInPath_notAuthorizedExceptionThrown() {
        var req = mockValidServletRequest();
        req.setAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, null);
        UserNotAuthorizedException ae = assertThrows(UserNotAuthorizedException.class,
                () -> interceptor.validateUserDetails(req),
                Constants.NOT_AUTHORIZED_MESSAGE);

        assertThat(ae).isNotNull();
        assertThat(ae.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);

        req.setAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, Map.of());
        ae = assertThrows(UserNotAuthorizedException.class,
                () -> interceptor.validateUserDetails(req),
                Constants.NOT_AUTHORIZED_MESSAGE);

        assertThat(ae).isNotNull();
        assertThat(ae.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);

        req.setAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, Map.of("studentId", ""));
        ae = assertThrows(UserNotAuthorizedException.class,
                () -> interceptor.validateUserDetails(req),
                Constants.NOT_AUTHORIZED_MESSAGE);

        assertThat(ae).isNotNull();
        assertThat(ae.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("Throw exception when request to controller has a non-numeric student ID.")
    void interceptRequestWithNonNumberIdInPath_notAuthorizedExceptionThrown() {
        var req = mockValidServletRequest();

        req.setAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, Map.of("studentId", "not-id"));
        UserNotAuthorizedException ae = assertThrows(UserNotAuthorizedException.class,
                () -> interceptor.validateUserDetails(req),
                Constants.NOT_AUTHORIZED_MESSAGE);

        assertThat(ae).isNotNull();
        assertThat(ae.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("Throw exception when request to controller has claims with a mis-matched ID to the path parameter.")
    void interceptRequestWithStudentIdNotEqualToClaims_notAuthorizedExceptionThrown() {
        var req1 = mockValidServletRequest();

        req1.setAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, Map.of("studentId", "5"));
        UserNotAuthorizedException ae = assertThrows(UserNotAuthorizedException.class,
                () -> interceptor.validateUserDetails(req1),
                Constants.NOT_AUTHORIZED_MESSAGE);

        assertThat(ae).isNotNull();
        assertThat(ae.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);

        var req2 = mockValidServletRequest();

        req2.setAttribute(Constants.CLAIMS, JwtHelper.buildValidClaims("4", "4"));
        ae = assertThrows(UserNotAuthorizedException.class,
                () -> interceptor.validateUserDetails(req2),
                Constants.NOT_AUTHORIZED_MESSAGE);

        assertThat(ae).isNotNull();
        assertThat(ae.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("Throw exception when request to controller has a non-numeric session ID in JWT.")
    void interceptRequestWithNonNumberSessionId_notAuthorizedExceptionThrown() {
        var req = mockValidServletRequest();
        req.setAttribute(Constants.CLAIMS, JwtHelper.buildValidClaims("2", "no-num"));

        UserNotAuthorizedException ae = assertThrows(UserNotAuthorizedException.class,
                () -> interceptor.validateUserDetails(req),
                Constants.NOT_AUTHORIZED_MESSAGE);

        assertThat(ae).isNotNull();
        assertThat(ae.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    private MockHttpServletRequest mockValidServletRequest() {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setAttribute(Constants.CLAIMS, JwtHelper.buildDefaultValidClaims());
        Map<String, String> map = new HashMap<>();
        map.put("studentId", "2");
        req.setAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, map);
        return req;
    }
}
