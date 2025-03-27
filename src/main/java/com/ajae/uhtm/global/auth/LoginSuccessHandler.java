package com.ajae.uhtm.global.auth;

import com.ajae.uhtm.global.utils.CookieUtil;
import com.ajae.uhtm.global.utils.JwtTokenFactory;
import com.ajae.uhtm.global.utils.JwtVerifier;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenFactory jwtTokenFactory;
    private final JwtVerifier jwtVerifier;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String accessToken = jwtTokenFactory.createAccessToken(authentication);
        String refreshToken = jwtTokenFactory.createRefreshToken(authentication);

        setTokensAndCookies(accessToken, refreshToken, response);

        log.info("authentication: {}", authentication.getAuthorities());
        response.sendRedirect("http://localhost:3000/index");
    }

    private void setTokensAndCookies(String accessToken, String refreshToken, HttpServletResponse response) {
        // JWT에서 직접 만료 시간을 추출하여 쿠키의 유효기간을 설정
        int accessTokenMaxAge = jwtVerifier.getExpiryDurationFromToken(accessToken);
        int refreshTokenMaxAge = jwtVerifier.getExpiryDurationFromToken(refreshToken);

        // 쿠키에 새 토큰 저장
        CookieUtil.createCookie(response, "accessToken", accessTokenMaxAge);
        CookieUtil.createCookie(response, "refreshToken", refreshTokenMaxAge);

        log.info("새로운 AccessToken: {}", accessToken);
        log.info("새로운 RefreshToken: {}", refreshToken);
    }
}
