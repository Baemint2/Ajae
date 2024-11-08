package ajae.uhtm.repository.userJoke;

import ajae.uhtm.entity.*;
import ajae.uhtm.repository.joke.JokeRepository;
import ajae.uhtm.repository.user.UserRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;

import static ajae.uhtm.entity.QJoke.joke;
import static ajae.uhtm.entity.QUser.user;
import static ajae.uhtm.entity.QUserJoke.userJoke;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserJokeRepositoryTest {

    @Autowired
    private JokeRepository jokeRepository;

    JPAQueryFactory queryFactory;

    @Autowired
    EntityManager em;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserJokeRepository userJokeRepository;

    User testUser;

    Joke testJoke, testJoke2;

    UserJoke testUserJoke, testUserJoke2;

    @BeforeEach
    public void init() {
        queryFactory = new JPAQueryFactory(em);

        testUser = User.builder()
                .providerKey("testProvider")
                .nickname("모지희")
                .build();

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

        User saveUser = userRepository.save(testUser);
        Joke saveJoke = jokeRepository.save(testJoke);
        Joke saveJoke2 = jokeRepository.save(testJoke2);

        testUserJoke = UserJoke.builder()
                .user(saveUser)
                .joke(saveJoke)
                .build();

        testUserJoke2 = UserJoke.builder()
                .user(saveUser)
                .joke(saveJoke2)
                .build();
    }

    @Test
    @DisplayName("유저개그를 저장한다.")
    void saveUserJoke() {
        User user1 = userRepository.findByUsername("moz1mozi").orElseThrow(IllegalArgumentException::new);
        UserJoke userJoke1 = UserJoke.builder()
                .user(user1)
                .joke(testJoke)
                .build();
        UserJoke saveUserJoke = userJokeRepository.save(userJoke1);

        assertNotNull(saveUserJoke);
        assertThat(saveUserJoke.getJoke()).isEqualTo(testJoke);

    }

    @Test
    @DisplayName("유저개그 리스트를 조회한다.")
    void getAllUserJokes() {
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<UserJoke> userJokePage = userJokeRepository.findAllByJokeJokeType(JokeType.USER_ADDED, pageRequest);
        for (UserJoke userJoke1 : userJokePage) {
            System.out.println(userJoke1.getJoke().toDto().toString());
        }
        assertThat(userJokePage).isNotNull();
    }

    @Test
    @DisplayName("특정 유저의 개그를 상세 조회한다.")
    void getUserJokeDetails() {
        userJokeRepository.save(testUserJoke);
        UserJoke userJoke1 = queryFactory.selectFrom(userJoke)
                .where(joke.id.eq(testJoke.getId()))
                .where(user.providerKey.eq(testUser.getProviderKey()))
                .fetchOne();

        assertThat(userJoke1).isNotNull();
        assertThat(userJoke1.getJoke()).isEqualTo(testJoke);
    }

    @Test
    @DisplayName("특정 유저의 유저 개그 리스트를 조회한다.")
    void getUserJokeById() {
        userJokeRepository.save(testUserJoke);
        userJokeRepository.save(testUserJoke2);
        List<UserJoke> userJokeList = queryFactory.selectFrom(userJoke)
                .where(user.id.eq(testUser.getId()))
                .fetch();

        for (UserJoke userJoke1 : userJokeList) {
            System.out.println("userJoke1 = " + userJoke1.toDto().getJoke());
        }
    }

    @Test
    @DisplayName("특정 유저가 추가한 유저 개그의 개수 체크")
    void getUserJokeCount() {
        userJokeRepository.save(testUserJoke);
        userJokeRepository.save(testUserJoke2);

        Long l = queryFactory.select(userJoke.count())
                .from(userJoke)
                .where(user.id.eq(testUser.getId()))
                .fetchOne();

        log.info("userCount = {}", l);
    }

    @Test
    @DisplayName("해당 개그를 특정 유저가 등록 했는지 안했는지 체크")
    void 개그_체크() {
        boolean checkUserJoke = queryFactory.selectFrom(userJoke)
                .where(user.id.eq(testUser.getId()), joke.id.eq(testJoke.getId()))
                .fetchOne() != null;
        System.out.println("checkUserJoke = " + checkUserJoke);
    }
}