package ajae.uhtm.service;

import ajae.uhtm.dto.JokeDto;
import ajae.uhtm.entity.Joke;
import ajae.uhtm.entity.JokeType;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
class JokeServiceTest {

    @Autowired
    private JokeService jokeService;

    @Test
    void getJoke() {
        JokeDto joke = jokeService.getRandomJoke();
        assertThat(joke).isNotNull();

        joke.toString();
    }

    @Test
    @Transactional
    void saveJoke() {
        Joke request = Joke.builder()
                .question("시가 현실적이면?")
                .answer("시리얼")
                .build();
        jokeService.saveJoke(request.toDto());
        assertThat(request.getJokeType()).isEqualTo(JokeType.DEFAULT);

    }
}