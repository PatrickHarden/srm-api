package com.scholastic.srmapi.helper;

import com.scholastic.srmapi.model.User;
import com.scholastic.srmapi.util.Constants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;

public class JwtHelper {
    public static Claims buildDefaultValidClaims() {
        return buildValidClaims("2", "3");
    }

    public static User buildDefaultValidUser() {
        return buildValidUser(2L, 3L);
    }

    public static User buildValidUser(Long subject, Long sessionId) {
        var c = new User();
        c.setSourceId(subject);
        c.setSessionId(sessionId);
        return c;
    }

    public static Claims buildValidClaims(String subject, String sessionId) {
        var c = new DefaultClaims().setSubject(subject);
        c.put(Constants.SESSION_ID, sessionId);
        return c;
    }
}
