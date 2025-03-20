package com.ajae.uhtm.global.auth.oauth2;

import com.ajae.uhtm.dto.user.OauthProviderConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class OauthProviderConfigService {

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

    public OauthProviderConfig getProviders(String registrationId) {

        Map<String, OauthProviderConfig> providerConfig = Map.of(
                "google", new OauthProviderConfig(googleClientId, googleRedirectUri, googleClientSecret, googleTokenUri),
                "kakao", new OauthProviderConfig(kakaoClientId, kakaoRedirectUrl, kakaoClientSecret, kakaoTokenUri),
                "naver", new OauthProviderConfig(naverClientId, naverRedirectUri, naverClientSecret, naverTokenUri)
        );

        OauthProviderConfig config = providerConfig.get(registrationId);
        if (config == null) {
            throw new IllegalArgumentException("지원되지 않는 제공자입니다. : " + registrationId);
        }
        return config;
    }
}

