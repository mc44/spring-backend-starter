package com.mfajardo.spring_backend_starter.controller;

import com.mfajardo.spring_backend_starter.dto.LoginDto;
import com.mfajardo.spring_backend_starter.dto.RefreshTokenDto;
import com.mfajardo.spring_backend_starter.entity.User;
import com.mfajardo.spring_backend_starter.service.AuthService;
import com.mfajardo.spring_backend_starter.service.AuthSessionStore;
import com.mfajardo.spring_backend_starter.service.JwtService;
import com.mfajardo.spring_backend_starter.service.UserDetailsServiceImpl;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserDetailsServiceImpl userService;
    private final JwtService jwtService;
    private final AuthService authService;
    private final AuthSessionStore sessionStore;

    @PostMapping("/login")
    @Transactional
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
        try {
            User user = authService.loginUser(loginDto);

            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);
            String newId = jwtService.extractTokenId(refreshToken);

            sessionStore.createSession(
                    newId,
                    user.getId().toString(),
                    jwtService.extractExpiration(refreshToken)
            );


            Map<String, Object> response = new HashMap<>();
            response.put("accessToken", accessToken);
            response.put("refreshToken", refreshToken);

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid credentials"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Login failed"));
        }
    }

    @PostMapping("/logout")
    @Transactional
    public ResponseEntity<?> logout(@RequestBody RefreshTokenDto request) {
        String token = jwtService.extractTokenId(request.getRefreshToken());
        sessionStore.invalidateSession(token);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("message", "Refresh Token Invalidated"));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(
            @RequestBody RefreshTokenDto request
    ) {
        try {
            String refreshToken = request.getRefreshToken();

            User user = authService.loadUserFromRefreshToken(refreshToken);

            Map<String, String> tokens =
                    authService.refreshTokens(refreshToken, user);

            return ResponseEntity.ok(tokens);

        } catch (JwtException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid or expired refresh token"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Could not refresh token"));
        }
    }

    @GetMapping("/me/authorities")
    public ResponseEntity<?> myAuthorities(Authentication authentication) {
        return ResponseEntity.ok(
                authentication.getAuthorities()
                        .stream()
                        .map(a -> a.getAuthority())
                        .toList()
        );
    }


}


