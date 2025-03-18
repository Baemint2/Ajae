package ajae.uhtm.service;

import com.ajae.uhtm.domain.joke.Joke;
import com.ajae.uhtm.domain.joke.JokeType;
import com.ajae.uhtm.domain.user.User;
import com.ajae.uhtm.domain.userJoke.UserJoke;
import com.ajae.uhtm.dto.UserJokeDto;
import com.ajae.uhtm.dto.joke.JokeDto;
import com.ajae.uhtm.repository.userjoke.UserJokeRepository;
import com.ajae.uhtm.service.UserJokeService;
import com.ajae.uhtm.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    private UserService userService;

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
        List<UserJoke> userJokes = List.of(testUserJoke, testUserJoke2);
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<UserJoke> userJokePage = new PageImpl<>(userJokes, pageRequest, userJokes.size());
        when(userJokeRepository.findAllByJokeJokeType(JokeType.USER_ADDED, pageRequest)).thenReturn(userJokePage);
        Page<UserJokeDto> allUserJokes = userJokeService.getAllUserJokes(0);
        assertThat(allUserJokes).isNotEmpty();

        System.out.println("allUserJokes = " + allUserJokes.getNumberOfElements());
    }

    @Test
    @Transactional
    @DisplayName("유저 개그가 하나도 없다면 Exception 발생")
    void getAllUserJokeIsEmpty() {
        List<UserJoke> userJokes = List.of();
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<UserJoke> userJokePage = new PageImpl<>(userJokes, pageRequest, 0);
        when(userJokeRepository.findAllByJokeJokeType(JokeType.USER_ADDED, pageRequest)).thenReturn(userJokePage);

        assertThrows(IllegalArgumentException.class, () -> userJokeService.getAllUserJokes(0),
                "유저가 추가한 개그가 존재하지 않습니다.");
    }

    @Test
    @Transactional
    @DisplayName("특정 유저의 특정 개그를 상세 조회한다.")
    void getUserJokeDetails() {
        when(userJokeRepository.selectUserJoke(testJoke.getId(), testUser.getId())).thenReturn(testUserJoke);
        UserJokeDto userJokeDetails = userJokeService.getUserJokeDetails(String.valueOf(testJoke.getId()), String.valueOf(testUser.getId()));
        assertNotNull(userJokeDetails);
    }

    @Test
    @Transactional
    @DisplayName("특정 유저의 유저 개그 리스트를 조회한다.")
    void getUserJokeById() {
    when(userJokeRepository.selectUserJokeById(testUser.getId())).thenReturn(List.of(testUserJoke, testUserJoke2));
        List<JokeDto> userJokeById = userJokeService.findAllJokesByUserId(testUser.getId());
        for (JokeDto userJokeDto : userJokeById) {
            System.out.println("userJokeDto = " + userJokeDto);
        }
    }

    @Test
    @Transactional
    @DisplayName("특정 유저가 추가한 유저 개그의 개수를 조회한다")
    void getUserJokeCountById() {
    when(userJokeRepository.countUserJoke(testUser.getId())).thenReturn(2L);
        Long userJokeCount = userJokeService.countUserJoke(testUser.getId());
        assertThat(userJokeCount).isEqualTo(2L);
    }

    @Test
    @Transactional
    @DisplayName("해당 개그를 특정 유저가 등록 했는지 안했는지 체크 (성공)")
    void existsUserJokeByUserId() {
        when(userService.findByUsername(testUser.getProviderKey())).thenReturn(testUser);
        when(userJokeRepository.existsUserJokeByUserId(testUser.getId(), testJoke.getId())).thenReturn(true);
        Boolean userJoke = userJokeService.existsUserJokeByUserId(testUser.getId(), testJoke.getId());
        System.out.println("b = " + userJoke);
    }

    @Test
    @Transactional
    @DisplayName("해당 개그를 특정 유저가 등록 했는지 안했는지 체크 (실패)")
    void existsUserJokeByUserId_fail() {
        when(userService.findByUsername(testUser.getProviderKey())).thenReturn(testUser);
        when(userJokeRepository.existsUserJokeByUserId(testUser.getId(), testJoke.getId())).thenReturn(false);
        Boolean userJoke = userJokeService.existsUserJokeByUserId(testUser.getId(), testJoke.getId());
        System.out.println("b = " + userJoke);
    }
}