package com.mfajardo.spring_backend_starter.service.impl;

import com.mfajardo.spring_backend_starter.dto.LoginDto;
import com.mfajardo.spring_backend_starter.entity.User;
import com.mfajardo.spring_backend_starter.repository.UserInfoRepository;
import com.mfajardo.spring_backend_starter.service.AuthService;
import com.mfajardo.spring_backend_starter.service.AuthSessionStore;
import com.mfajardo.spring_backend_starter.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserInfoRepository userInfoRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AuthSessionStore sessionStore;

    @Override
    public User loginUser(LoginDto loginDto) throws BadRequestException {

        if (loginDto.getPassword() == null || loginDto.getPassword().isBlank()) {
            throw new BadRequestException("Password must not be empty");
        }

        try {
            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    loginDto.getUsername(),
                                    loginDto.getPassword()
                            )
                    );
            if (authentication.isAuthenticated()) {
                return userInfoRepository.findByUsername(loginDto.getUsername()).orElse(null);
            }else{
                throw new BadRequestException("Password is incorrect");
            }

        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }


    @Override
    public User loadUserFromRefreshToken(String refreshToken) {
        String username = jwtService.extractUsername(refreshToken);
        return userInfoRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public Map<String, String> refreshTokens(
            String refreshToken,
            User user
    ) {
        if (jwtService.isTokenExpired(refreshToken)) {
            throw new RuntimeException("Refresh token expired");
        }

        if (!jwtService.isRefreshToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        String username = jwtService.extractUsername(refreshToken);
        if (!username.equals(user.getUsername())) {
            throw new RuntimeException("Refresh token user mismatch");
        }

        sessionStore.invalidateSession(refreshToken);

        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);
        String newId = jwtService.extractTokenId(newRefreshToken);

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
