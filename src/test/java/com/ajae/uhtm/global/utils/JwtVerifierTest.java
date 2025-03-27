package com.ajae.uhtm.global.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;

import javax.crypto.SecretKey;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
@SpringBootTest
class JwtVerifierTest {

    @Autowired
    JwtVerifier jwtVerifier;

    @Autowired
    JwtTokenFactory jwtTokenFactory;

    @Value("${jwt.secret}")
    String secretKey;

    SecretKey key;

    @BeforeEach
    void setUp() {
        key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    @WithMockUser
    @DisplayName("생성된 access token을 검증한다.")
    @Test
    void verifyJwt() {
        // given // when
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String accessToken = jwtTokenFactory.createAccessToken(auth);
        Claims claims = jwtVerifier.verify(accessToken);

        // then
        assertThat(claims.getSubject()).isEqualTo("user");
        assertThat(claims)
                .containsEntry("username", "user")
                .containsEntry("role", "ROLE_USER");
    }

    @WithMockUser
    @DisplayName("변조된 토큰을 검증한다.")  
    @Test  
    void verifyModulatedToken() {
        // given  
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String accessToken = jwtTokenFactory.createAccessToken(auth);
        
        // when
        String modulatedToken = accessToken.substring(0, accessToken.length() - 1) + "asdasd";

        // then
        assertThatThrownBy(() -> jwtVerifier.verify(modulatedToken))
                .isInstanceOf(JwtException.class);
    }

    @DisplayName("만료된 토큰을 검증한다.")
    @Test
    void verifyExpiredToken() {
        // given // when
        String expiredToken = Jwts.builder()
                .issuer("test")
                .subject("expiredUser")
                .expiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(key)
                .compact();

        // then
        assertThatThrownBy(() -> jwtVerifier.verify(expiredToken))
                .isInstanceOf(ExpiredJwtException.class);
    }
}