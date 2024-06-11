package com.scholastic.srmapi.filter;

import com.scholastic.srmapi.util.JwtTokenProvider;
import com.scholastic.srmapi.exception.UserNotAuthorizedException;
import com.scholastic.srmapi.util.Constants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.ServletException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

class JwtAuthenticationFilterTest {

    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtTokenProvider);
    }

    @Test
    @DisplayName("Perform claims filtering on valid request.")
    void doFilterInternal_responseSuccessful() throws Exception {
        String token = "test";
        Claims claims = new DefaultClaims();
        claims.setSubject("1");
        claims.put(Constants.SESSION_ID, "2");
        MockHttpServletRequest request = new MockHttpServletRequest();
        when(jwtTokenProvider.deriveJwtFromCookie(request)).thenReturn(token);
        when(jwtTokenProvider.translateJwtToClaims(token)).thenReturn(claims);

        MockHttpServletResponse response = new MockHttpServletResponse();

        jwtAuthenticationFilter.doFilterInternal(request, response, new MockFilterChain());
        assertEquals(200, response.getStatus());
    }

    @Test
    @DisplayName("Filtering on request produces an unauthorized request with internal exception")
    void doFilterInternalException_returnUnauthorized() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        doThrow(new UserNotAuthorizedException(Constants.NOT_AUTHORIZED_MESSAGE))
                .when(jwtTokenProvider)
                .deriveJwtFromCookie(request);
        MockHttpServletResponse response = new MockHttpServletResponse();
        jwtAuthenticationFilter.doFilterInternal(request, response, new MockFilterChain());
        assertEquals(401, response.getStatus());
        assertEquals(Constants.NOT_AUTHORIZED_MESSAGE, response.getContentAsString());
    }

    @Test
    @DisplayName("Filtering on request produces an unauthorized request with empty subject")
    void doFilterEmptySubjectJwt_returnUnauthorized() throws Exception {
        String token = "test";
        Claims claims = new DefaultClaims();
        MockHttpServletRequest request = new MockHttpServletRequest();
        when(jwtTokenProvider.deriveJwtFromCookie(request)).thenReturn(token);
        when(jwtTokenProvider.translateJwtToClaims(token)).thenReturn(claims);
        MockHttpServletResponse response = new MockHttpServletResponse();
        jwtAuthenticationFilter.doFilterInternal(request, response, new MockFilterChain());
        assertEquals(401, response.getStatus());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/info", "/health", "/lti"})
    @DisplayName("Filtering on request produces a valid request for whitelisted endpoints")
    void doFilterUserNotAuthorized_noExceptionWithUriOnWhitelist(String whitelistedUrl) throws IOException, ServletException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI(whitelistedUrl);
        MockHttpServletResponse response = new MockHttpServletResponse();
        jwtAuthenticationFilter.doFilterInternal(request, response, new MockFilterChain());
        assertEquals(200, response.getStatus());
    }

    @Test
    @DisplayName("Filtering on request with no cookie and bad URL produces unauth response.")
    void doFilterNoCookieWithNonWhitelistedUrl_returnUnauthorized() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("not-valid-filter");
        when(jwtTokenProvider.deriveJwtFromCookie(request)).thenReturn(null);
        MockHttpServletResponse response = new MockHttpServletResponse();
        jwtAuthenticationFilter.doFilterInternal(request, response, new MockFilterChain());
        assertEquals(401, response.getStatus());
    }


}
