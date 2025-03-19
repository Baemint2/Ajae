package com.ajae.uhtm.dto.user;

import lombok.Builder;
import lombok.Getter;

@Getter
public class OauthProviderConfig {
    private String clientId;
    private String redirectUri;
    private String clientSecret;
    private String tokenUri;

    @Builder
    public OauthProviderConfig(String clientId, String redirectUri, String clientSecret, String tokenUri) {
        this.clientId = clientId;
        this.redirectUri = redirectUri;
        this.clientSecret = clientSecret;
        this.tokenUri = tokenUri;
    }
}
