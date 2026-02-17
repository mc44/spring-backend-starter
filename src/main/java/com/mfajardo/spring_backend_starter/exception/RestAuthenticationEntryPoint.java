package com.mfajardo.spring_backend_starter.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

@Component
@Slf4j
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        log.warn("Authentication failed: {}", authException.getMessage());

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        String message;
        if (authException instanceof BadCredentialsException) {
            message = "Invalid username or password";
        } else if (authException instanceof LockedException) {
            message = "Account locked";
        } else if (authException instanceof DisabledException) {
            message = "Account disabled";
        } else {
            message = "Authentication failed";
        }

        String body = """
        {
          "timestamp": "%s",
          "status": 401,
          "error": "Unauthorized",
          "message": "%s",
          "path": "%s",
          "traceId": "%s"
        }
        """.formatted(Instant.now(), message, request.getRequestURI(), MDC.get("traceId"));

        response.getWriter().write(body);
    }
}

