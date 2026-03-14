package com.mfajardo.spring_backend_starter;

import com.mfajardo.spring_backend_starter.controller.AuthController;
import com.mfajardo.spring_backend_starter.entity.User;
import com.mfajardo.spring_backend_starter.filter.JwtAuthFilter;
import com.mfajardo.spring_backend_starter.service.AuthService;
import com.mfajardo.spring_backend_starter.service.AuthSessionStore;
import com.mfajardo.spring_backend_starter.service.JwtService;
import com.mfajardo.spring_backend_starter.service.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = AuthController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthFilter.class
        )
)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    AuthService authService;

    @MockitoBean
    JwtService jwtService;

    @MockitoBean
    AuthSessionStore sessionStore;

    @MockitoBean
    UserDetailsServiceImpl userService;

    @Test
    void login_shouldReturnTokens_whenValidCredentials() throws Exception {

        User user = new User();
        user.setId(UUID.randomUUID());

        when(authService.loginUser(any())).thenReturn(user);
        when(jwtService.generateAccessToken(user)).thenReturn("access-token");
        when(jwtService.generateRefreshToken(user)).thenReturn("refresh-token");
        when(jwtService.extractTokenId("refresh-token")).thenReturn("token-id");
        when(jwtService.extractExpiration("refresh-token")).thenReturn(new Date());

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {"username":"admin","password":"admin123"}
                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"));
    }

    @Test
    void login_shouldReturnBadRequest_whenInvalidCredentials() throws Exception {
        when(authService.loginUser(any()))
                .thenThrow(new RuntimeException("Bad credentials"));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"admin","password":"wrongpassword"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_shouldReturnValidationError_whenMissingUsername() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"password":"admin123"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void refreshToken_shouldReturnNewTokens() throws Exception {
        User user = new User();
        user.setId(UUID.randomUUID());

        Map<String, String> newTokens = new HashMap<>();
        newTokens.put("accessToken", "new-access-token");
        newTokens.put("refreshToken", "new-refresh-token");

        when(authService.loadUserFromRefreshToken("refresh-token")).thenReturn(user);
        when(authService.refreshTokens("refresh-token", user)).thenReturn(newTokens);

        mockMvc.perform(post("/api/v1/auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"refreshToken":"refresh-token"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new-access-token"))
                .andExpect(jsonPath("$.refreshToken").value("new-refresh-token"));
    }

    @Test
    void logout_shouldInvalidateRefreshToken() throws Exception {
        doNothing().when(sessionStore).invalidateSession("token-id");
        when(jwtService.extractTokenId("refresh-token")).thenReturn("token-id");

        mockMvc.perform(post("/api/v1/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"refreshToken":"refresh-token"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Refresh Token Invalidated"));
    }
}

