package com.ajae.uhtm.domain.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    USER("유저"),
    ADMIN("관리자");

    private final String text;
}
