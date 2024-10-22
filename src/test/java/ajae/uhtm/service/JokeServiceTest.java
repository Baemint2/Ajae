package ajae.uhtm.service;

import ajae.uhtm.dto.JokeDto;
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
    void saveJoke() {
        JokeDto request = new JokeDto("시가 현실적이면?", "시리얼");
        Long l = jokeService.saveJoke(request);
    }
}