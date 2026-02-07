package com.mfajardo.spring_backend_starter.service;

import java.util.Date;

public interface AuthSessionStore {

        void createSession(String refreshTokenId, String userId, Date expiresAt);

        boolean isSessionValid(String refreshTokenId);

        void invalidateSession(String refreshTokenId);

        void invalidateAllForUser(String userId);
}
