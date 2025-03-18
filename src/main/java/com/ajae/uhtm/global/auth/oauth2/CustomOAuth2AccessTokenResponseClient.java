package com.ajae.uhtm.global.auth.oauth2;

import com.ajae.uhtm.dto.TokenResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class CustomOAuth2AccessTokenResponseClient implements OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> {

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    String kakaoClientId;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    String kakaoClientSecret;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    String kakaoRedirectUrl;

    @Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
    String kakaoTokenUri;

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    String naverClientId;

    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    String naverClientSecret;

    @Value("${spring.security.oauth2.client.registration.naver.redirect-uri}")
    String naverRedirectUri;

    @Value("${spring.security.oauth2.client.provider.naver.token-uri}")
    String naverTokenUri;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    String googleClientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    String googleRedirectUri;

    @Value("${spring.security.oauth2.client.provider.google.token-uri}")
    String googleTokenUri;

    String grantType = "authorization_code";

    @Override
    public OAuth2AccessTokenResponse getTokenResponse(OAuth2AuthorizationCodeGrantRequest authorizationGrantRequest) {
        RestTemplate rt = new RestTemplate();  // http요청을 쉽게 할 수 있는 라이브러리

        String code = authorizationGrantRequest
                        .getAuthorizationExchange()
                        .getAuthorizationResponse()
                        .getCode();
        // HttpHeaders 오브젝트 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        String registrationId = authorizationGrantRequest.getClientRegistration().getRegistrationId();
        TokenResponse tokenResponse = requestToken(code, headers, rt, registrationId);

        return OAuth2AccessTokenResponse.withToken(tokenResponse.getAccessToken()) // 실제 토큰 값으로 변경해야 함
                .tokenType(OAuth2AccessToken.TokenType.BEARER)
                .expiresIn(3600) // 만료 시간 설정 (초)
                .build();
    }

    private TokenResponse requestToken(String code, HttpHeaders headers, RestTemplate rt, String registrationId) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", grantType); // 값을 변수화하는게 낫다
        params.add("code", code);

        switch (registrationId) {
            case "google":
                params.add("client_id", googleClientId);
                params.add("redirect_uri", googleRedirectUri);
                params.add("client_secret", googleClientSecret);
                return exchangeToken(kakaoTokenUri, headers, params, rt);
            case "naver":
                params.add("client_id", naverClientId);
                params.add("redirect_uri", naverRedirectUri);
                params.add("client_secret", naverClientSecret);
                return exchangeToken(naverTokenUri, headers, params, rt);
            case "kakao":
                params.add("client_id", kakaoClientId);
                params.add("redirect_uri", kakaoRedirectUrl);
                params.add("client_secret", kakaoClientSecret);
                return exchangeToken(kakaoTokenUri, headers, params, rt);
            default:
                throw new IllegalArgumentException("지원되지 않는 제공자입니다. : " + registrationId);
        }
    }

    private TokenResponse exchangeToken(String tokenUri, HttpHeaders headers, MultiValueMap<String, String> params, RestTemplate rt) {
        HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = rt.exchange(tokenUri, HttpMethod.POST, tokenRequest, String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(response.getBody(), TokenResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
