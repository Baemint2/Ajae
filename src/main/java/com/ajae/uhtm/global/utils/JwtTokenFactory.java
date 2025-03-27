package com.ajae.uhtm.global.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.stream.Collectors;

@Getter
@Component
public class JwtTokenFactory {

    public static final String ISSUER = "moz1mozi.com";
    public static final int EXP_SHORT = 15 * 60 * 1000; // 15분
    public static final int EXP_LONG = 60 * 60 * 1000;  // 1시간
    public static final int REFRESH_EXP = 7 * 24 * 60 * 60 * 1000; // 7일

    private SecretKey key;

    public JwtTokenFactory(@Value("${jwt.secret}")String secretKey) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    public String createAccessToken(Authentication auth) {
        String username = auth.getName();
        String authorities = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .issuer(ISSUER)
                .subject(username)
                .expiration(new Date(System.currentTimeMillis() + EXP_LONG))
                .claim("username", username)
                .claim("role", authorities)
                .signWith(key)
                .compact();
    }

    public String createRefreshToken(Authentication auth) {
        String username = auth.getName();

        return Jwts.builder()
                .issuer(ISSUER)
                .subject(username)
                .expiration(new Date(System.currentTimeMillis() + REFRESH_EXP))
                .signWith(key)
                .compact();
    }
}
