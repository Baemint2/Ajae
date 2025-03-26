package com.ajae.uhtm.repository;

import com.ajae.uhtm.domain.joke.Joke;
import com.ajae.uhtm.repository.joke.JokeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static com.ajae.uhtm.domain.joke.JokeType.DEFAULT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

@ActiveProfiles("test")
@SpringBootTest
class JokeRepositoryTest {

    @Autowired
    JokeRepository jokeRepository;

    @Test
    @DisplayName("Called가 False인 개그 리스트를 조회한다.")
    void findByCalledFalseAndJokeType() {
        // when
        List<Joke> byCalledFalseAndJokeType = jokeRepository.findByCalledFalseAndJokeType(DEFAULT);

        // then
        assertThat(byCalledFalseAndJokeType).hasSize(3)
                .extracting("question", "answer", "jokeType", "called")
                .containsExactlyInAnyOrder(
                        tuple("공이 남아도는 나라는?", "남아공", DEFAULT, false),
                        tuple("가위를 잘 쓰는 왕은?", "핑킹", DEFAULT, false),
                        tuple("가장 바쁜 떡은?", "싼 게 비지떡", DEFAULT, false));
    }
}
