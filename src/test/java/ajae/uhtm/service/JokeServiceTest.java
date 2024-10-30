package ajae.uhtm.service;

import ajae.uhtm.dto.joke.JokeDto;
import ajae.uhtm.entity.Joke;
import ajae.uhtm.entity.JokeType;
import ajae.uhtm.entity.User;
import ajae.uhtm.entity.UserJoke;
import ajae.uhtm.repository.joke.JokeRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
class JokeServiceTest {

    @Autowired
    private JokeService jokeService;

    @Autowired
    private UserJokeService userJokeService;

    @Autowired
    private UserService userService;

    @Autowired
    private JokeRepository jokeRepository;

    @Value("${provider-key.kakao}")
    String kakaoKey;

    @DisplayName("개그를 한 개 가져온다.")
    @Test
    void getJoke() {
        JokeDto joke = jokeService.getRandomJoke();
        assertThat(joke).isNotNull();

        log.info("joke.toString: {}", joke.toString());
    }

    @DisplayName("유저가 개그를 추가한다.")
    @Test
    void saveJoke() {
        String username = kakaoKey;
        Joke request = Joke.builder()
                .question("시가 현실적이면?")
                .answer("시리얼")
                .build();

        Long saveJoke = jokeService.saveJoke(request, username);
        Joke joke = jokeService.findById(saveJoke);
        User byUsername = userService.findByUsername(username);

        // UserJoke 저장
        UserJoke userJoke = UserJoke.builder()
                .joke(joke)
                .user(byUsername)
                .build();

        long l = userJokeService.saveUserJoke(userJoke);
        assertThat(l).isEqualTo(1);

        // 만약 joke 저장하면서 userJoke까지 추가하고 싶으면
        // then
        assertThat(joke.getQuestion()).isEqualTo("시가 현실적이면?");
        assertThat(joke.getAnswer()).isEqualTo("시리얼");
        assertThat(joke.getJokeType()).isEqualTo(JokeType.USER_ADDED);
    }

    @DisplayName("유저가 추가한 개그를 리스트로 조회한다.")
    @Test
    void getUserJoke() {
        List<Joke> userJoke = jokeRepository.findByCalledFalseAndJokeType(JokeType.USER_ADDED);
        log.info("userJoke: {}", userJoke);
    }
}