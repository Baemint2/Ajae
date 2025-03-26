package com.ajae.uhtm.domain.joke;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JokeType {
    DEFAULT("기본"),
    USER_ADDED("유저추가");

    private final String text;


}
