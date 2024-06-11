package com.scholastic.srmapi.controller;

import java.io.UnsupportedEncodingException;
import java.net.URI;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scholastic.srmapi.service.LtiService;
import com.scholastic.srmapi.util.Constants;
import com.scholastic.srmapi.util.Encoding;
import com.scholastic.srmapi.util.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = Constants.LTI, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class LTILaunchController {
    private static final int COOKIE_EXPIRATION_TIME_IN_SECS = 1 * 24 * 60 * 60; // 1 day

    private final LtiService ltiService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping
    public ResponseEntity<Void> authenticateUser(HttpServletRequest request, HttpServletResponse response)
            throws UnsupportedEncodingException {
        if (!ltiService.validateLtiRequest(request)) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

        var redirectURL = request.getParameter(Constants.REDIRECT_URL);
        var sdmNavCtx = request.getParameter(Constants.CUSTOM_SDM_NAV_CTX);
        var userId = request.getParameter(Constants.USER_ID);

        if (redirectURL == null || "".equals(redirectURL)) {
            // default redirect is to root "/" (react FE)
            redirectURL = Constants.ROOT;
        }

        Long sessionId = ltiService.createUserSession(userId);

        // set sdm_nav_ctx cookie
        response.addCookie(createCookie(Constants.SDM_NAV_CTX, Encoding.percentEncode(sdmNavCtx)));
        // set jwt cookie
        response.addCookie(createCookie(Constants.LITPLAT_JWT,
                jwtTokenProvider.generateJwt(userId, sessionId.toString())));

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(redirectURL))
                .build();
    }

    /**
     * Helper method to create a cookie
     * 
     * @param name  the name of the cookie
     * @param value the value of the cookie
     * @return the {@code Cookie} object
     */
    private Cookie createCookie(String name, String value) {
        var cookie = new Cookie(name, value);
        cookie.setMaxAge(COOKIE_EXPIRATION_TIME_IN_SECS);
        cookie.setSecure(true);
        cookie.setDomain(Constants.COOKIE_DOMAIN_NAME);
        return cookie;
    }
}
