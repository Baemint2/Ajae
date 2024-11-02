package ajae.uhtm.service;

import ajae.uhtm.dto.UserJokeDto;
import ajae.uhtm.entity.Joke;
import ajae.uhtm.entity.JokeType;
import ajae.uhtm.entity.User;
import ajae.uhtm.entity.UserJoke;
import ajae.uhtm.repository.joke.JokeRepository;
import ajae.uhtm.repository.user.UserRepository;
import ajae.uhtm.repository.userJoke.UserJokeRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@Slf4j
@SpringBootTest
class UserJokeServiceTest {

    @InjectMocks
    private UserJokeService userJokeService;

    @Mock
    private UserJokeRepository userJokeRepository;

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

        testUserJoke2 = UserJoke.builder()
                .joke(testJoke2)
                .user(testUser)
                .build();
        testUserJoke.testUserJokeId(2L);
    }

    @Test
    @Transactional
    @DisplayName("유저 개그를 저장한다.")
    void saveUserJoke() {
        when(userJokeRepository.save(testUserJoke)).thenReturn(testUserJoke);
        UserJoke userJoke = userJokeService.saveUserJoke(testUserJoke);
        assertNotNull(userJoke);
        assertThat(userJoke.getId()).isEqualTo(testUserJoke.getId());

    }

    @Test
    @Transactional
    @DisplayName("유저 개그 리스트를 조회한다.")
    void getAllUserJoke() {
        when(userJokeRepository.selectAllUserJoke(JokeType.USER_ADDED)).thenReturn(List.of(testUserJoke,testUserJoke2));
        List<UserJokeDto> allUserJokes = userJokeService.getAllUserJokes();

        assertThat(allUserJokes).isNotEmpty();
        assertThat(allUserJokes.size()).isEqualTo(2);

        for (UserJokeDto allUserJoke : allUserJokes) {
            log.info("allUserJoke.toString: {}", allUserJoke.getJoke());
        }
    }

    @Test
    @Transactional
    @DisplayName("유저 개그가 하나도 없다면 Exception 발생")
    void getAllUserJokeIsEmpty() {
        when(userJokeRepository.selectAllUserJoke(JokeType.USER_ADDED)).thenReturn(List.of());

        assertThrows(IllegalArgumentException.class, () -> userJokeService.getAllUserJokes(),
                "유저가 추가한 개그가 존재하지 않습니다.");
    }



}