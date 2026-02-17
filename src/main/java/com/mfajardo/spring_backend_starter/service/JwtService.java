package com.mfajardo.spring_backend_starter.service;

import com.mfajardo.spring_backend_starter.entity.User;
import com.mfajardo.spring_backend_starter.exception.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

import static io.jsonwebtoken.SignatureAlgorithm.HS256;

@Service
public class JwtService {

    @Value("${application.security.jwt.secret.key}")
    private String secretKey;

    @Value("${application.security.jwt.access.expiration}")
    private long accessExpiration;

    @Value("${application.security.jwt.refresh.expiration}")
    private long refreshExpiration;

    /* ===================== TOKEN CREATION ===================== */

    public String generateAccessToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("typ", "ACCESS")
                .setIssuedAt(new Date())
                .setExpiration(nowPlus(accessExpiration))
                .signWith(getSignKey(), HS256)
                .compact();
    }

    public String generateRefreshToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("typ", "REFRESH")
                .claim("jti", UUID.randomUUID().toString())
                .setIssuedAt(new Date())
                .setExpiration(nowPlus(refreshExpiration))
                .signWith(getSignKey(), HS256)
                .compact();
    }

    /* ===================== VALIDATION ===================== */

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean isRefreshToken(String token) {
        return "REFRESH".equals(extractClaim(token, c -> c.get("typ")));
    }

    /* ===================== CLAIMS ===================== */

    public String extractUsername(String token) {
        try {
            return extractClaim(token, Claims::getSubject);
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidTokenException();
        }
    }

    public String extractTokenId(String token) {
        try {
            return extractClaim(token, claims -> claims.get("jti", String.class));
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidTokenException();
        }
    }

    public Date extractExpiration(String token) {
        try {
            return extractClaim(token, Claims::getExpiration);
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidTokenException();
        }
    }


    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        try {
            return resolver.apply(extractAllClaims(token));
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidTokenException();
        }
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidTokenException();
        }
    }


    /* ===================== UTIL ===================== */

    private Date nowPlus(long millis) {
        return new Date(System.currentTimeMillis() + millis);
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
