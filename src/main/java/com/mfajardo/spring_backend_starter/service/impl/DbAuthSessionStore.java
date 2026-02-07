package com.mfajardo.spring_backend_starter.service.impl;

import com.mfajardo.spring_backend_starter.entity.AuthSession;
import com.mfajardo.spring_backend_starter.repository.AuthSessionRepository;
import com.mfajardo.spring_backend_starter.service.AuthSessionStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@ConditionalOnProperty(
        name = "application.security.session.store",
        havingValue = "db",
        matchIfMissing = true
)
public class DbAuthSessionStore implements AuthSessionStore {

    private final AuthSessionRepository repo;

    public DbAuthSessionStore(AuthSessionRepository repo) {
        this.repo = repo;
    }

    @Override
    public void createSession(String id, String userId, Date expiresAt) {
        repo.save(new AuthSession(id, userId, expiresAt, false));
    }

    @Override
    public boolean isSessionValid(String id) {
        return repo.findById(id)
                .filter(s -> !s.isRevoked())
                .filter(s -> s.getExpiresAt().after(new Date()))
                .isPresent();
    }

    @Override
    public void invalidateSession(String id) {
        repo.findById(id).ifPresent(s -> {
            s.setRevoked(true);
            repo.save(s);
        });
    }

    @Override
    public void invalidateAllForUser(String userId) {
        repo.findByUserIdAndRevokedFalse(userId)
                .forEach(s -> {
                    s.setRevoked(true);
                    repo.save(s);
                });
    }
}

