package com.mfajardo.spring_backend_starter.repository;

import com.mfajardo.spring_backend_starter.entity.AuthSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthSessionRepository extends JpaRepository<AuthSession, String> {

    List<AuthSession> findByUserIdAndRevokedFalse(String userId);

}

