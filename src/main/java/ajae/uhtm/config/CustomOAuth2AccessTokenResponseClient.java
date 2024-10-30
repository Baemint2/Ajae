package ajae.uhtm.config;

import ajae.uhtm.dto.TokenResponse;
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
import java.util.Objects;

public class CustomOAuth2AccessTokenResponseClient implements OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> {

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    String clientId;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    String clientSecret;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    String redirectUrl;

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

        // HttpBody 오브젝트 생성
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", grantType); // 값을 변수화하는게 낫다
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUrl);
        params.add("client_secret", clientSecret);
        params.add("code", code);

        // HttpHeaders 와 HttpBody 를 하나의 오브젝트에 담기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(params, headers); //바디 데이터와 와 헤더값을 가지는 entity가 된다

        // Http 요청하기 - POST방식으로 그리고 response 변수의 응답 받음
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token", //토큰 발급 요청 주소
                HttpMethod.POST, //요청 메서드 post
                kakaoTokenRequest,
                String.class // 응답받을 타입
        );
        System.out.println(response.getBody());
        ObjectMapper objectMapper = new ObjectMapper();
        TokenResponse tokenResponse = null;
        try {
            tokenResponse = objectMapper.readValue(response.getBody(), TokenResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return OAuth2AccessTokenResponse.withToken(tokenResponse.getAccessToken()) // 실제 토큰 값으로 변경해야 함
                .tokenType(OAuth2AccessToken.TokenType.BEARER)
                .expiresIn(3600) // 만료 시간 설정 (초)
                .build();
    }
}
