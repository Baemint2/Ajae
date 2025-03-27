package com.ajae.uhtm.global.utils;

import io.jsonwebtoken.Claims;
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

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
class JwtTokenFactoryTest {

    @Autowired
    JwtTokenFactory jwtTokenFactory;

    @Value("${jwt.secret}")
    String secretKey;

    SecretKey key;

    @BeforeEach
    void setUp() {
        key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    @Test
    @WithMockUser()
    @DisplayName("access token을 생성한다.")
    void createAccessToken() {
        // given // when
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String accessToken = jwtTokenFactory.createAccessToken(auth);
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(accessToken)
                .getPayload();

        // then
        assertThat(claims.getSubject()).isEqualTo("user");
        assertThat(claims).containsEntry("username", "user");
        assertThat(claims.get("role")).isNotNull();
    }

    @Test
    @WithMockUser()
    @DisplayName("refresh token을 생성한다.")
    void createRefreshToken() {

        // given // when
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String refreshToken = jwtTokenFactory.createRefreshToken(auth);
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(refreshToken)
                .getPayload();

        // then
        assertThat(claims.getSubject()).isEqualTo("user");
    }
}