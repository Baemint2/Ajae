package com.ajae.uhtm.domain.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProviderType {
    KAKAO("카카오"),
    NAVER("네이버"),
    GOOGLE("구글");

    private final String text;
}
