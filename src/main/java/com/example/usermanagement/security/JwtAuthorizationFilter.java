package com.example.usermanagement.security;

import com.example.usermanagement.config.UserContext;
import com.example.usermanagement.config.UserSessionInfo;
import com.example.usermanagement.exception.NoAuthenticationError;
import com.nimbusds.jwt.JWTClaimsSet;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import java.io.IOException;

@Component
@Slf4j
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private static final String HEADER = "Authorization";
    private static final String PREFIX = "Bearer ";
    private final JwtService jwtService;
    private final UserSessionService sessionService;
    private final HandlerExceptionResolver resolver;

    public JwtAuthorizationFilter(
            JwtService jwtService,
            UserSessionService sessionService,
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.jwtService = jwtService;
        this.sessionService = sessionService;
        this.resolver = resolver;
        }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = resolveToken(request);
            if (StringUtils.hasText(token)) {
                JWTClaimsSet claimsSet = jwtService.verifyAndParse(token);
                String username = claimsSet.getSubject();
                if (!sessionService.isValidToken(username, token)) {
                    throw new NoAuthenticationError("Chưa đăng nhập hoặc token không hợp lệ!");
                }

                UserSessionInfo sessionInfo = sessionService.getUserSession(username)
                        .orElseThrow(() -> new NoAuthenticationError("Chưa đăng nhập hoặc token không hợp lệ!"));
                UserContext.set(sessionInfo);

                Authentication authentication = jwtService.getAuthentication(token, claimsSet);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (NoAuthenticationError error) {
            log.error("Authentication failed: {} - {}", error.getStatus(), error.getMessageVi());
            resolver.resolveException(request, response, null, error);
            SecurityContextHolder.clearContext();
            UserContext.clear();
            return;
        } try {
            filterChain.doFilter(request, response); // luôn gọi đúng 1 lần
        } finally {
            UserContext.clear();
            SecurityContextHolder.clearContext();
        }
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(PREFIX)) {
            return bearerToken.substring(7);
        }
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("access_token".equals(cookie.getName()) && StringUtils.hasText(cookie.getValue())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
