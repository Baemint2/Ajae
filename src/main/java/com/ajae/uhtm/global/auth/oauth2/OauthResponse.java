package com.ajae.uhtm.global.auth.oauth2;


import com.ajae.uhtm.domain.user.ProviderType;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OauthResponse {
    String providerId;
    String email;
    String profile;
    String nickname;
    ProviderType providerType;

    @Builder
    private OauthResponse(String providerId, String email, String profile, String nickname, ProviderType providerType) {
        this.providerId = providerId;
        this.email = email;
        this.profile = profile;
        this.nickname = nickname;
        this.providerType = providerType;
    }

    public static OauthResponse create(String providerId, String email, String profile, String nickname, ProviderType providerType) {
        return OauthResponse.builder()
                .providerId(providerId)
                .email(email)
                .profile(profile)
                .nickname(nickname)
                .providerType(providerType)
                .build();
    }

}
