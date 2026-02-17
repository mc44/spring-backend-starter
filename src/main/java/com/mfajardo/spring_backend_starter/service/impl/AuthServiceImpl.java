package com.mfajardo.spring_backend_starter.service.impl;

import com.mfajardo.spring_backend_starter.dto.LoginDto;
import com.mfajardo.spring_backend_starter.entity.User;
import com.mfajardo.spring_backend_starter.exception.AppException;
import com.mfajardo.spring_backend_starter.exception.InvalidTokenException;
import com.mfajardo.spring_backend_starter.repository.UserInfoRepository;
import com.mfajardo.spring_backend_starter.service.AuthService;
import com.mfajardo.spring_backend_starter.service.AuthSessionStore;
import com.mfajardo.spring_backend_starter.service.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserInfoRepository userInfoRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AuthSessionStore sessionStore;

    @Override
    @Transactional
    public User loginUser(LoginDto loginDto){

        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                    loginDto.getUsername(),
                    loginDto.getPassword()
            )
        );

        return userInfoRepository.findByUsername(loginDto.getUsername())
                .orElseThrow(() -> {
                    log.error("CRITICAL: Authenticated user '{}' not found in DB", loginDto.getUsername());
                    return new AppException("Authentication state invalid", HttpStatus.INTERNAL_SERVER_ERROR) {};
                });
    }

    public User loadUserFromRefreshToken(String refreshToken) {
        try {
            String username = jwtService.extractUsername(refreshToken);

            return userInfoRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidTokenException();
        }
    }

    public Map<String, String> refreshTokens(String refreshToken, User user) {

        // invalidate old session
        sessionStore.invalidateSession(refreshToken);

        // generate new tokens
        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);
        String newId = jwtService.extractTokenId(newRefreshToken);

        // store new session
        sessionStore.createSession(
                newId,
                user.getId().toString(),
                jwtService.extractExpiration(newRefreshToken)
        );

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", newAccessToken);
        tokens.put("refreshToken", newRefreshToken);

        return tokens;
    }

}
