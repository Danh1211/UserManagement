package com.example.usermanagement.security;

import com.example.usermanagement.config.UserContext;
import com.example.usermanagement.config.UserSessionInfo;
import com.example.usermanagement.enums.ErrorMessage;
import com.example.usermanagement.exception.AppException;
import com.nimbusds.jwt.JWTClaimsSet;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private static final String HEADER = "Authorization";
    private static final String PREFIX = "Bearer ";
    private final JwtService jwtService;
    private final UserSessionService sessionService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = resolveToken(request);
            if(StringUtils.hasText(token)){
                JWTClaimsSet claimsSet = jwtService.verifyAndParse(token);
                String username = claimsSet.getSubject();
                if(!sessionService.isValidToken(username, token)){
                    throw AppException.builder().status(ErrorMessage.INVALID_JWT).build();
                }

                UserSessionInfo sessionInfo = sessionService.getUserSession(username)
                        .orElseThrow(() -> AppException.builder().status(ErrorMessage.INVALID_JWT).build());
                UserContext.set(sessionInfo);

                Authentication authentication = jwtService.getAuthentication(token, claimsSet);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (AppException e) {
            log.error("Authentication failed: {} - {}", e.getStatus().getErrorCode(), e.getStatus().getTitle());
            SecurityContextHolder.clearContext();
            UserContext.clear();
        } try {
            filterChain.doFilter(request, response);   // luôn gọi đúng 1 lần
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
        return null;
    }
}
