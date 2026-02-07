package com.mfajardo.spring_backend_starter;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@RequiredArgsConstructor
@ContextConfiguration(classes = PasswordEncoderTest.TestConfig.class)
class PasswordEncoderTest {

    private final PasswordEncoder encoder;

    @Test
    void generateHash() {
        System.out.println(encoder.encode("placeholder"));
    }

    @Configuration
    static class TestConfig {
        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }
}
