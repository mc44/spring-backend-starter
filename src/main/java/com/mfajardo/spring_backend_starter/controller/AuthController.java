package com.mfajardo.spring_backend_starter.controller;

import com.mfajardo.spring_backend_starter.dto.LoginDto;
import com.mfajardo.spring_backend_starter.dto.RefreshTokenDto;
import com.mfajardo.spring_backend_starter.entity.User;
import com.mfajardo.spring_backend_starter.logging.LogEvent;
import com.mfajardo.spring_backend_starter.service.AuthService;
import com.mfajardo.spring_backend_starter.service.AuthSessionStore;
import com.mfajardo.spring_backend_starter.service.JwtService;
import com.mfajardo.spring_backend_starter.service.UserDetailsServiceImpl;
import io.jsonwebtoken.JwtException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
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

    @LogEvent(action = "LOGIN")
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDto loginDto) throws BadRequestException {
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
    public ResponseEntity<Map<String, String>> refreshToken(
            @RequestBody RefreshTokenDto request
    ) {
        String refreshToken = request.getRefreshToken();

        User user = authService.loadUserFromRefreshToken(refreshToken);

        Map<String, String> tokens =
                authService.refreshTokens(refreshToken, user);

        return ResponseEntity.ok(tokens);
    }

    @GetMapping("/me/authorities")
    public ResponseEntity<?> myAuthorities(Authentication authentication) {
        return ResponseEntity.ok(
                authentication.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList()
        );
    }


}


