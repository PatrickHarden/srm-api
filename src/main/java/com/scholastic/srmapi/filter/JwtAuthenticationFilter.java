package com.scholastic.srmapi.filter;

import com.scholastic.srmapi.exception.UserNotAuthorizedException;
import com.scholastic.srmapi.model.User;
import com.scholastic.srmapi.model.UserSession;
import com.scholastic.srmapi.util.Constants;
import com.scholastic.srmapi.util.JwtTokenProvider;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

/**
 * Validates Jwt token and saves Claims to request.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws IOException, ServletException {
        try {
            String cookie = jwtTokenProvider.deriveJwtFromCookie(request);
            if (cookie != null) {
                var claims = Optional.of(jwtTokenProvider.translateJwtToClaims(cookie))
                        .filter(c -> JwtTokenProvider.validateIdIsValid(c.getSubject()))
                        .filter(c -> JwtTokenProvider.validateIdIsValid(c.get(Constants.SESSION_ID, String.class)))
                        .orElseThrow(() -> new UserNotAuthorizedException(Constants.NOT_AUTHORIZED_MESSAGE));

                UsernamePasswordAuthenticationToken authenticationToken = Optional.of(claims.getSubject())
                        .map(Long::parseLong)
                        .map(id -> {
                            var user = new User();
                            user.setSourceId(id);
                            user.setSessionId(JwtTokenProvider.deriveSessionIdFromClaims(claims));
                            user.setAuthorities(Collections.singletonList("ROLE_USER"));
                             return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                        }).orElseThrow(() -> new UserNotAuthorizedException(Constants.NOT_AUTHORIZED_MESSAGE));

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                request.setAttribute(Constants.CLAIMS, claims);
            } else {
                String uri = request.getRequestURI();
                if (!uri.equals("/info") && !uri.equals("/lti") && !uri.equals("/health")) {
                    throw new UserNotAuthorizedException(Constants.NOT_AUTHORIZED_MESSAGE);
                }
            }
            filterChain.doFilter(request, response);
        } catch (UserNotAuthorizedException e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write(e.getMessage());
        }
    }

    @Bean
    @RequestScope
    public UserSession userSession() {
        return new UserSession();
    }
}
