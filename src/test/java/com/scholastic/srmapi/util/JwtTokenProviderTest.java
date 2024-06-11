package com.scholastic.srmapi.util;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;

import com.scholastic.srmapi.helper.JwtHelper;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {
    private final String JWT_SECRET = "blahblahblahblahblahblahblahblah";
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private HttpServletRequest request;


    @BeforeEach
    void setup() {
        jwtTokenProvider = new JwtTokenProvider(JWT_SECRET);
    }
    @Test
    void generateJwtGeneratesTokenWithUserIdInSub() throws IOException {
        var jwt = jwtTokenProvider.generateJwt("123456", "1");
        var payloadString = Base64.getDecoder().decode(jwt.split("\\.")[1]);
        var payload = new ObjectMapper().readValue(payloadString, Map.class);
        assertEquals("123456", payload.get("sub").toString());
    }

    @Test
    @DisplayName("Jwt translates with correct sub details")
    void translateJwtWithUserIdInSub_correctSubReturned() {
        var jwt = jwtTokenProvider.generateJwt("123456", "1");
        var sub = jwtTokenProvider.translateJwtToSub(jwt);

        assertNotNull(sub);
        assertThat(sub).isEqualTo("123456");
    }

    @Test
    @DisplayName("Invalid JWT for translation produces an exception")
    void translateInvalidJwt_badSessionExceptionThrown(){
        JwtException ae = assertThrows(JwtException.class,
                () -> jwtTokenProvider.translateJwtToSub("invalidjwt.with.periods"),
                Constants.MISSING_SESSION_INFORMATION_MESSAGE);

        assertEquals(Constants.MISSING_SESSION_INFORMATION_MESSAGE, ae.getMessage());
    }

    @Test
    @DisplayName("Valid JWT verified returns correct source ID.")
    void verifySession_sessionDetailsRetrieved() {
        var jwt = jwtTokenProvider.generateJwt("123456", "1");
        var reqCookies = new Cookie[] {new Cookie(Constants.LITPLAT_JWT, jwt)};
        when(request.getCookies()).thenReturn(reqCookies);

        var session = jwtTokenProvider.verifySessionAndReturnSourceId(request );

        assertThat(session).isNotNull();
        assertThat(session).isEqualTo(123456);
    }

    @Test
    @DisplayName("Verifying JWT translation fails due to a malformed source ID")
    void verifyBadSessionId_throwBadRequest() {
        var jwt = jwtTokenProvider.generateJwt("123456a", "1");
        var reqCookies = new Cookie[] {new Cookie(Constants.LITPLAT_JWT, jwt)};
        when(request.getCookies()).thenReturn(reqCookies);

        JwtException ae = assertThrows(JwtException.class,
                () -> jwtTokenProvider.verifySessionAndReturnSourceId(request),
                Constants.MISSING_SESSION_INFORMATION_MESSAGE);

        assertEquals(Constants.MISSING_SESSION_INFORMATION_MESSAGE, ae.getMessage());
    }

    @Test
    @DisplayName("Verifying JWT translation fails due to a malformed JWT")
    void verifyBadSessionJwt_throwBadRequest() {
        var jwt = "notvalid";
        var reqCookies = new Cookie[] {new Cookie(Constants.LITPLAT_JWT, jwt)};
        when(request.getCookies()).thenReturn(reqCookies);

        JwtException ae = assertThrows(JwtException.class,
                () -> jwtTokenProvider.verifySessionAndReturnSourceId(request),
                Constants.MISSING_SESSION_INFORMATION_MESSAGE);

        assertEquals(Constants.MISSING_SESSION_INFORMATION_MESSAGE, ae.getMessage());
    }

    @Test
    @DisplayName("Verifying JWT fails due to empty cookie")
    void verifyBadSession_throwBadRequest() {
        var reqCookies = new Cookie[0];
        when(request.getCookies()).thenReturn(reqCookies);

        JwtException ae = assertThrows(JwtException.class,
                () -> jwtTokenProvider.verifySessionAndReturnSourceId(request),
                Constants.MISSING_SESSION_INFORMATION_MESSAGE);

        assertEquals(Constants.MISSING_SESSION_INFORMATION_MESSAGE, ae.getMessage());
    }

    @Test
    @DisplayName("Verifying JWT fails due to no cookie")
    void verifyNoSession_throwBadRequest() {
        JwtException ae = assertThrows(JwtException.class,
                () -> jwtTokenProvider.verifySessionAndReturnSourceId(request),
                Constants.MISSING_SESSION_INFORMATION_MESSAGE);

        assertEquals(Constants.MISSING_SESSION_INFORMATION_MESSAGE, ae.getMessage());
    }

    @Test
    @DisplayName("Verifying JWT translation fails due to no valid cookie")
    void translateEmptyCookie_translationReturnsNull() {
        assertThat(jwtTokenProvider.translateJwtToSub(null)).isNull();
        assertThat(jwtTokenProvider.translateJwtToSub("test")).isNull();
    }

    @Test
    @DisplayName("Verify invalid claims and student IDs will correctly be recognized as not matching.")
    void verifyClaimsMatchingStudentIdNotMatchingOrValid_returnsFalse() {
        assertThat(JwtTokenProvider.validateClaimsMatchStudentId(JwtHelper.buildDefaultValidClaims(), null)).isFalse();
        assertThat(JwtTokenProvider.validateClaimsMatchStudentId(JwtHelper.buildDefaultValidClaims(), "")).isFalse();
        assertThat(JwtTokenProvider.validateClaimsMatchStudentId(JwtHelper.buildDefaultValidClaims(), "NaN")).isFalse();

        assertThat(JwtTokenProvider.validateClaimsMatchStudentId(JwtHelper.buildValidClaims(null, "1"), "1")).isFalse();
        assertThat(JwtTokenProvider.validateClaimsMatchStudentId(JwtHelper.buildValidClaims("", "1"), "1")).isFalse();
        assertThat(JwtTokenProvider.validateClaimsMatchStudentId(JwtHelper.buildValidClaims("NaN", "1"), "1")).isFalse();
        assertThat(JwtTokenProvider.validateClaimsMatchStudentId(JwtHelper.buildValidClaims("NaN", "1"), "NaN")).isFalse();

    }

    @Test
    @DisplayName("Verify invalid IDs recognized as invalid.")
    void verifyIDsNotValid_returnsFalse() {
        assertThat(JwtTokenProvider.validateIdIsValid(null)).isFalse();
        assertThat(JwtTokenProvider.validateIdIsValid("")).isFalse();
        assertThat(JwtTokenProvider.validateIdIsValid("NaN")).isFalse();
        assertThat(JwtTokenProvider.validateIdIsValid("-1")).isFalse();
    }
}
