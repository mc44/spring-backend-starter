package com.mfajardo.spring_backend_starter.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "auth_sessions")
public class AuthSession {

    @Id
    private String refreshTokenId;

    private String userId;

    private Date expiresAt;

    private boolean revoked;
}

