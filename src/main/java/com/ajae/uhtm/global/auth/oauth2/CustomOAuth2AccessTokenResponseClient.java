package com.ajae.uhtm.global.auth.oauth2;

import com.ajae.uhtm.dto.TokenResponse;
import com.ajae.uhtm.dto.user.OauthProviderConfig;
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

import java.util.Map;

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
        TokenResponse tokenResponse = requestToken(authorizationGrantRequest);

        return OAuth2AccessTokenResponse.withToken(tokenResponse.getAccessToken()) // 실제 토큰 값으로 변경해야 함
                .tokenType(OAuth2AccessToken.TokenType.BEARER)
                .expiresIn(3600) // 만료 시간 설정 (초)
                .build();
    }

    private TokenResponse requestToken(OAuth2AuthorizationCodeGrantRequest oauth2GrantRequest) {
        RestTemplate rt = new RestTemplate();

        String code = oauth2GrantRequest
                .getAuthorizationExchange()
                .getAuthorizationResponse()
                .getCode();

        // HttpHeaders 오브젝트 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        String registrationId = oauth2GrantRequest
                .getClientRegistration()
                .getRegistrationId();

        return requestToken(code, headers, rt, registrationId);
    }

    private TokenResponse requestToken(String code, HttpHeaders headers, RestTemplate rt, String registrationId) {

        Map<String, OauthProviderConfig> providerConfig = Map.of(
                "google", new OauthProviderConfig(googleClientId, googleRedirectUri, googleClientSecret, googleTokenUri),
                "kakao", new OauthProviderConfig(kakaoClientId, kakaoRedirectUrl, kakaoClientSecret, kakaoTokenUri),
                "naver", new OauthProviderConfig(naverClientId, naverRedirectUri, naverClientSecret, naverTokenUri)
        );

        OauthProviderConfig config = providerConfig.get(registrationId);
        if (config == null) {
            throw new IllegalArgumentException("지원되지 않는 제공자입니다. : " + registrationId);
        }

        MultiValueMap<String, String> params = createTokenRequestParams(code, config);
        return exchangeToken(config.getTokenUri(), headers, params, rt);
    }

    private MultiValueMap<String, String> createTokenRequestParams(String code, OauthProviderConfig config) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", grantType);
        params.add("code", code);
        params.add("client_id", config.getClientId());
        params.add("redirect_uri", config.getRedirectUri());
        params.add("client_secret", config.getClientSecret());
        return params;
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
