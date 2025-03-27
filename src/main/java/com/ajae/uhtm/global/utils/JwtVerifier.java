package com.ajae.uhtm.global.utils;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtVerifier {

    private final JwtTokenFactory jwtTokenFactory;

    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER = "Authorization";

    public Claims verify(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(jwtTokenFactory.getKey())
                    .build()
                    .parseSignedClaims(token.replace(TOKEN_PREFIX, ""))
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.error("JWT expired at: {}. Current time: {}", e.getClaims().getExpiration(), new Date());
            throw e;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            throw e;
        }
    }

    public int getExpiryDurationFromToken(String token) {
        Claims claims = verify(token);
        long expirationMillis = claims.getExpiration().getTime();
        long currentMillis = System.currentTimeMillis();
        return (int) ((expirationMillis - currentMillis) / 1000);
    }

    public String getUsernameFromToken(String token) {
        Claims claims = verify(token);
        log.info("getUsernameFromToken : {} {}", token, claims.getSubject());
        return claims.getSubject();
    }

    public UserDetails getUserDetailsFromToken(String token) {
        Claims claims = verify(token);
        String username = claims.getSubject();
        return new User(username, "", List.of());
    }

    public boolean validateToken(String authToken) {
        try {
            verify(authToken);
            return true;
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
        }
        return false;
    }
}
