package com.scholastic.srmapi.util;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Arrays;
import java.util.Optional;

import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.DefaultClaims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.security.Keys;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@Component
public class JwtTokenProvider {
    private static final String ISSUER = "SRM";
    private final String jwtSecret;

    public JwtTokenProvider(@Value("${jwtSecret}") final String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

    /**
     * Generates JWT
     * 
     * @param userId the user id
     * @return jwt {@code String}
     */
    public String generateJwt(String userId, String sessionId) {
        var now = Instant.now();
        var issuedAt = Date.from(now);
        var expiration = Date.from(now.plus(1, ChronoUnit.DAYS));
        var key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .setIssuer(ISSUER)
                .setSubject(userId)
                .setIssuedAt(issuedAt)
                .claim(Constants.SESSION_ID, sessionId)
                .setExpiration(expiration)
                .signWith(key)
                .compact();
    }

    /**
     * Translates a JWT from its Base64-encoded {@code String} into the subject of the JWT.
     * @param cookie The Base64-encoded {@code String} cookie.
     * @return The String representation of the subject.
     */
    public String translateJwtToSub(String cookie) {
        return Optional.of(translateJwtToClaims(cookie))
                .map(Claims::getSubject)
                .orElse(null);
    }

    /**
     * Translates a JWT from its Base64-encoded {@code String} into Claims.
     * @param cookie The Base64-encoded {@code String} cookie.
     * @return A populated Claims object of the cookie.
     */
    public Claims translateJwtToClaims(String cookie) {
        if (cookie != null && cookie.contains(".")) {
            try {
                Key hmacKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

                Jws<Claims> jwt = Jwts.parserBuilder()
                        .setSigningKey(hmacKey)
                        .build()
                        .parseClaimsJws(cookie);
                return Optional.ofNullable(jwt)
                        .filter(j -> j.getBody() != null)
                        .map(Jwt::getBody)
                        .orElse(null);
            } catch (ClassCastException | MalformedJwtException e) {
                log.warn("Cookie was rejected: " + e);
                throw new JwtException(Constants.MISSING_SESSION_INFORMATION_MESSAGE);
            }
        }
        return new DefaultClaims();
    }

    /**
     * Verifies the JWT is valid, then pulls the source ID from the JWT.
     * @param req The HttpServletRequest made to the server.
     */
    public Long verifySessionAndReturnSourceId(HttpServletRequest req) {
        String jwt = deriveJwtFromCookie(req);

        if (jwt != null) {
            String sourceId = translateJwtToSub(jwt);
            if (sourceId != null) {
                try {
                    return Long.parseLong(sourceId);
                } catch (NumberFormatException e) {
                    log.info("Source was not a valid Long ID: " + sourceId);
                }
            }
        }

        throw new JwtException(Constants.MISSING_SESSION_INFORMATION_MESSAGE);
    }

    /**
     * Derives the JWT token from a given servlet request
     * @param request the request to be parsed
     * @return the JWT token in String form.
     */
    public String deriveJwtFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            return Arrays.stream(request.getCookies())
                    .filter(cookie -> Constants.LITPLAT_JWT.equals(cookie.getName()))
                    .map(Cookie::getValue)
                    .findFirst().orElse(null);
        }
        return null;
    }

    /**
     * Validates equality between populated Claims and student ID.
     * @param claims Claims from JWT.
     * @param id student ID.
     * @return if the two params equal.
     */
    public static boolean validateClaimsMatchStudentId(Claims claims, String id) {
        if (id != null && validateIdIsValid(id) && claims != null && !claims.isEmpty() ) {
            return id.equals(claims.getSubject());
        }
        return false;
    }

    /**
     * Validates equality between populated Claims and student ID.
     * @param claims Claims from JWT.
     * @return The Long ID of the session associated to the JWT
     */
    public static Long deriveSessionIdFromClaims(Claims claims) {
        return Optional.ofNullable(claims)
                .map(c -> c.get(Constants.SESSION_ID))
                .map(Object::toString)
                .filter(JwtTokenProvider::validateIdIsValid)
                .map(Long::parseLong)
                .orElse(null);
    }

    /**
     * Validates that an ID is a valid source or session Long string greater than 0.
     * @param id The ID to be checked
     * @return true if the ID can be parsed and is greater than 0.
     */
    public static boolean validateIdIsValid(String id) {
        try {
            Long.parseLong(id);
        } catch (Exception e) {
            return false;
        }
        return Long.parseLong(id) > 0;
    }
}
