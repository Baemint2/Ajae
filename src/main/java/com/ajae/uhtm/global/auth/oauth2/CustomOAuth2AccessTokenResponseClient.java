package com.ajae.uhtm.global.auth.oauth2;

import com.ajae.uhtm.dto.TokenResponse;
import com.ajae.uhtm.dto.user.OauthProviderConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.Objects;

@RequiredArgsConstructor
public class CustomOAuth2AccessTokenResponseClient implements OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> {

    private final OauthProviderConfigService providerConfigService;

    public static final String GRANT_TYPE = "authorization_code";

    @Override
    public OAuth2AccessTokenResponse getTokenResponse(OAuth2AuthorizationCodeGrantRequest authorizationGrantRequest) {
        TokenResponse tokenResponse = requestToken(authorizationGrantRequest);

        return OAuth2AccessTokenResponse.withToken(tokenResponse.getAccessToken()) // 실제 토큰 값으로 변경해야 함
                .tokenType(OAuth2AccessToken.TokenType.BEARER)
                .expiresIn(3600) // 만료 시간 설정 (초)
                .build();
    }

    private TokenResponse requestToken(OAuth2AuthorizationCodeGrantRequest oauth2GrantRequest) {
        String code = getAuthorizationCode(oauth2GrantRequest);

        HttpHeaders headers = createHeaders();
        String registrationId = oauth2GrantRequest
                .getClientRegistration()
                .getRegistrationId();

        return requestToken(code, headers, registrationId);
    }

    private TokenResponse requestToken(String code, HttpHeaders headers, String registrationId) {
        RestClient restClient = RestClient.create();

        OauthProviderConfig config = providerConfigService.getProviders(registrationId);

        MultiValueMap<String, String> params = createTokenRequestParams(code, config);

        return exchangeToken(config.getTokenUri(), headers, params, restClient);
    }

    private static String getAuthorizationCode(OAuth2AuthorizationCodeGrantRequest oauth2GrantRequest) {
        return oauth2GrantRequest
                .getAuthorizationExchange()
                .getAuthorizationResponse()
                .getCode();
    }

    private MultiValueMap<String, String> createTokenRequestParams(String code, OauthProviderConfig config) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", GRANT_TYPE);
        params.add("code", code);
        params.add("client_id", config.getClientId());
        params.add("redirect_uri", config.getRedirectUri());
        params.add("client_secret", config.getClientSecret());
        return params;
    }

    private static HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        return headers;
    }

    private TokenResponse exchangeToken(String tokenUri, HttpHeaders headers, MultiValueMap<String, String> params, RestClient restClient) {
        HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(params, headers);
        String result = restClient.post()
                .uri(tokenUri)
                .headers(httpHeaders -> httpHeaders.addAll(tokenRequest.getHeaders()))
                .body(Objects.requireNonNull(tokenRequest.getBody()))
                .retrieve()
                .body(String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(result, TokenResponse.class);
        } catch (JsonProcessingException e) {
            throw new IOException(e.getMessage());
        }
    }

}
