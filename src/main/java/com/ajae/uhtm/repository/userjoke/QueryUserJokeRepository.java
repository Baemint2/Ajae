package com.ajae.uhtm.repository.userjoke;

import com.ajae.uhtm.domain.joke.JokeType;
import com.ajae.uhtm.domain.userJoke.UserJoke;

import java.util.List;

public interface QueryUserJokeRepository {

    List<UserJoke> selectAllUserJoke(JokeType jokeType);

    UserJoke selectUserJoke(long jokeId, long userId);

    List<UserJoke> selectUserJokeById(long userId);

    Long countUserJoke(long userId);

    // 유저개그가 존재하는지 체크
    Boolean existsUserJokeByUserId(Long userId, Long jokeId);
}
