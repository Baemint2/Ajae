package com.ajae.uhtm.repository;

import com.ajae.uhtm.domain.joke.Joke;
import com.ajae.uhtm.repository.joke.JokeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static com.ajae.uhtm.domain.joke.JokeType.USER_ADDED;

@ActiveProfiles("test")
@SpringBootTest
class JokeRepositoryTest {

    @Autowired
    JokeRepository jokeRepository;

    @Test
    void 레포지토리_테스트() {
        List<Joke> byCalledFalseAndJokeType = jokeRepository.findByCalledFalseAndJokeType(USER_ADDED);
    }
}
