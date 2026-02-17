package com.mfajardo.spring_backend_starter.filter;

import com.mfajardo.spring_backend_starter.exception.InvalidTokenException;
import com.mfajardo.spring_backend_starter.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.mfajardo.spring_backend_starter.config.SecurityConfig.WHITE_LIST_URL;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // skip JWT check for whitelist
        if (isWhitelisted(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new InvalidTokenException("Authorization header missing or invalid");
            }

            String token = authHeader.substring(7);
            String username = jwtService.extractUsername(token); // throws InvalidTokenException if malformed
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails user = userDetailsService.loadUserByUsername(username);

                if (jwtService.isTokenExpired(token)) {
                    throw new InvalidTokenException("Token expired");
                }

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        user, null, user.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

            filterChain.doFilter(request, response);

        } catch (InvalidTokenException ex) {
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("""
                {
                    "timestamp": "%s",
                    "status": 401,
                    "error": "Unauthorized",
                    "message": "%s",
                    "path": "%s",
                    "traceId": "%s"
                }
                """.formatted(java.time.Instant.now(), ex.getMessage(), request.getRequestURI(), null));
        }
    }

    private boolean isWhitelisted(String path) {
        for (String pattern : WHITE_LIST_URL) {
            if (pattern.endsWith("*") && path.startsWith(pattern.substring(0, pattern.length() - 1))) {
                return true;
            }
            if (pattern.equals(path)) {
                return true;
            }
        }
        return false;
    }


}
