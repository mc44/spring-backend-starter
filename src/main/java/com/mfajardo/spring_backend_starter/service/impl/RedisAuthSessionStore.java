package com.mfajardo.spring_backend_starter.service.impl;

import com.mfajardo.spring_backend_starter.service.AuthSessionStore;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Set;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(
        name = "application.security.session.store",
        havingValue = "redis"
)
public class RedisAuthSessionStore implements AuthSessionStore {

    private final RedisTemplate<String, String> redis;

    @Override
    public void createSession(String id, String userId, Date expiresAt) {
        Duration ttl = Duration.between(Instant.now(), expiresAt.toInstant());
        redis.opsForValue().set(id, userId, ttl);
        redis.opsForSet().add("user:" + userId + ":tokens", id);
    }

    @Override
    public boolean isSessionValid(String tokenId) {
        return Boolean.TRUE.equals(redis.hasKey(tokenId));
    }

    @Override
    public void invalidateSession(String tokenId) {
        String userId = redis.opsForValue().get(tokenId);
        if (userId != null) {
            redis.opsForSet()
                    .remove("user:" + userId + ":refresh_tokens", tokenId);
        }

        redis.delete(tokenId);
    }

    @Override
    public void invalidateAllForUser(String userId) {
        String key = "user:" + userId + ":tokens";

        Set<String> tokens = redis.opsForSet().members(key);
        if (tokens != null && !tokens.isEmpty()) {
            redis.delete(tokens);
        }

        redis.delete(key);
    }
}
