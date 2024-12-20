package ajae.uhtm.service;

import ajae.uhtm.dto.UserJokeDto;
import ajae.uhtm.dto.joke.JokeDto;
import ajae.uhtm.entity.Joke;
import ajae.uhtm.entity.JokeType;
import ajae.uhtm.entity.User;
import ajae.uhtm.entity.UserJoke;
import ajae.uhtm.repository.joke.JokeRepository;
import ajae.uhtm.repository.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Slf4j
@SpringBootTest
class JokeServiceTest {

    @InjectMocks
    private JokeService jokeService;

    @Mock
    private UserService userService;

    @Mock
    private UserJokeService userJokeService;

    @Mock
    private JokeRepository jokeRepository;

    @Mock
    private UserRepository userRepository;

    User testUser;

    Joke testJoke, testJoke2;

    UserJoke testUserJoke, testUserJoke2;


    @BeforeEach
    void init() {
        testUser = User.builder()
                .providerKey("testProvider")
                .nickname("모지희")
                .build();

        testUser.testUserId(1L);

        testJoke = Joke.builder()
                .question("개가 한 마리만 사는 나라는?")
                .answer("독일")
                .jokeType(JokeType.USER_ADDED)
                .build();

        testJoke2 = Joke.builder()
                .question("아몬드가 죽으면?")
                .answer("다이아몬드")
                .jokeType(JokeType.USER_ADDED)
                .build();

        testJoke.testJokeId(1L);
        testJoke2.testJokeId(2L);

        testUserJoke = UserJoke.builder()
                .joke(testJoke)
                .user(testUser)
                .build();
        testUserJoke.testUserJokeId(1L);

        when(jokeRepository.save(testJoke)).thenReturn(testJoke);

        testUserJoke2 = UserJoke.builder()
                .joke(testJoke2)
                .user(testUser)
                .build();
        testUserJoke.testUserJokeId(2L);

    }

    @Test
    @DisplayName("개그를 한 개 가져온다.")
    @Transactional
    void getJoke() {
        when(jokeRepository.findByCalledFalseAndJokeType(JokeType.DEFAULT)).thenReturn(List.of(testJoke, testJoke2));

        JokeDto joke = jokeService.getRandomJoke();
        assertThat(joke).isNotNull();

        log.info("joke.toString: {}", joke.toString());
    }

    @DisplayName("유저가 개그를 추가한다.")
    @Transactional
    @Test
    void saveJoke() {
        when(userService.findByUsername(testUser.getProviderKey())).thenReturn(testUser);
        when(jokeRepository.save(any(Joke.class))).thenAnswer(invocation -> {
            Joke joke = invocation.getArgument(0);
            // Simulate an auto-generated ID by setting it after save
            joke.testJokeId(1L);
            return joke;
        });
        Joke savedJoke = jokeService.saveJoke(testJoke, testUser.getProviderKey());
        assertThat(savedJoke.getQuestion()).isEqualTo("개가 한 마리만 사는 나라는?");
        assertThat(savedJoke.getAnswer()).isEqualTo("독일");
        assertThat(savedJoke.getJokeType()).isEqualTo(JokeType.USER_ADDED);
    }
}