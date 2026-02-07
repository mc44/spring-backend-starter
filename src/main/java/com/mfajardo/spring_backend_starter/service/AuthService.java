package com.mfajardo.spring_backend_starter.service;

import com.mfajardo.spring_backend_starter.dto.LoginDto;
import com.mfajardo.spring_backend_starter.entity.User;
import org.apache.coyote.BadRequestException;

import java.util.Map;

public interface AuthService {
    User loginUser(LoginDto loginDto) throws BadRequestException;

    User loadUserFromRefreshToken(String refreshToken);

    Map<String, String> refreshTokens(String refreshToken, User user);
}
