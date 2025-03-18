package com.ajae.uhtm.dto;

import com.ajae.uhtm.dto.joke.JokeDto;
import com.ajae.uhtm.dto.user.UserInfoDto;
import com.ajae.uhtm.domain.userJoke.UserJoke;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserJokeDto {

    private JokeDto joke;
    private UserInfoDto user;

    public UserJoke toEntity() {
        return UserJoke.builder()
                .user(user.toEntity())
                .joke(joke.toEntity())
                .build();
    }
}
