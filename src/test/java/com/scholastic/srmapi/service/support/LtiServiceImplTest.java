package com.scholastic.srmapi.service.support;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import com.scholastic.srmapi.model.UserLoginSession;
import com.scholastic.srmapi.repository.UserLoginSessionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class LtiServiceImplTest {
    @Mock
    private Environment env;

    @InjectMocks
    private LtiServiceImpl ltiServiceImpl;

    @Mock
    private UserLoginSessionRepository sessionRepository;

    @Test
    void validateLtiRequestReturnsFalseWhenSignatureIsIncorrect() {
        when(env.getProperty(anyString())).thenReturn("secret");
        var request = mockLTIRequest("signature");
        ReflectionTestUtils.setField(ltiServiceImpl, "srmWebRootUrl", "http://localhost:8080");

        assertFalse(ltiServiceImpl.validateLtiRequest(request));
    }

    @Test
    void validateLtiRequestReturnsTrueWhenSignatureIsCorrect() {
        when(env.getProperty(anyString())).thenReturn("secret");
        var request = mockLTIRequest("tuwYtWuXqfyw/O/pkHmwQjjh4uc=");
        ReflectionTestUtils.setField(ltiServiceImpl, "srmWebRootUrl", "http://localhost:8080/");

        assertTrue(ltiServiceImpl.validateLtiRequest(request));
    }

    @Test
    void saveSession_validateSessionCreated() {
        var session = new UserLoginSession();
        session.setId(10L);

        when(sessionRepository.save(any(UserLoginSession.class))).thenReturn(session);
        var sessionId = ltiServiceImpl.createUserSession("3");

        assertEquals(10L, sessionId);
    }

    private HttpServletRequest mockLTIRequest(String signature) {
        var request = new MockHttpServletRequest();
        var consumerKey = "consumerKey";
        request.addParameter("oauth_signature", signature);
        request.addParameter("oauth_consumer_key", consumerKey);
        request.addParameter("oauth_signature_method", "HMAC-SHA1");
        request.addParameter("parameter_with_carriage_return", "hello\r\nworld");

        return request;
    }
}
