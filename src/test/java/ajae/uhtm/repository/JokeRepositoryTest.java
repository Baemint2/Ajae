package ajae.uhtm.repository;

import ajae.uhtm.entity.*;
import ajae.uhtm.repository.joke.JokeRepository;
import ajae.uhtm.repository.user.UserRepository;
import ajae.uhtm.repository.userJoke.UserJokeRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

import static ajae.uhtm.entity.QJoke.joke;
import static ajae.uhtm.entity.QUser.user;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.util.Assert.isInstanceOf;

@Slf4j
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class JokeRepositoryTest {

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

    Joke testJoke;

    UserJoke testUserJoke;

    @BeforeEach
    public void init() {
        queryFactory = new JPAQueryFactory(em);

        testUser = User.builder()
                .username("testProvider")
                .username("모지희")
                .build();
        
        testJoke = Joke.builder()
                .question("개가 한 마리만 사는 나라는?")
                .answer("독일")
                .jokeType(JokeType.USER_ADDED)
                .build();

        User saveUser = userRepository.save(testUser);
        Joke saveJoke = jokeRepository.save(testJoke);

        testUserJoke = UserJoke.builder()
                .user(saveUser)
                .joke(saveJoke)
                .build();

        userJokeRepository.save(testUserJoke);

    }

    @Test
    @Transactional
    void 랜덤인덱스_호출() {

        // false 사이즈를 구해서 rand
        List<Joke> byCalledFalse = jokeRepository.findByCalledFalseAndJokeType(JokeType.DEFAULT);
        log.info("byCalledFalse.size() {}", byCalledFalse.size());
        // called 가 전부 true 이면 동작
        if (byCalledFalse.isEmpty()) {
            log.info("called가 전부 true 입니다. 초기화 작업을 진행합니다.");
            jokeRepository.resetCalledStatus();
            byCalledFalse = jokeRepository.findByCalledFalseAndJokeType(JokeType.DEFAULT);
        }

        int size = byCalledFalse.size();
        int rand = new Random().nextInt(size);
        Joke joke = byCalledFalse.get(rand);

        joke.updateCalled();
        jokeRepository.save(joke);

        assertThat(joke).isNotNull();
        log.info("문제: {}", joke.getQuestion());
        log.info("정답: {}", joke.getAnswer());
    }

    @Test
    @Transactional
    void 특정인덱스_체크() {

        Joke joke = jokeRepository.findById(testJoke.getId()).orElseThrow(IllegalArgumentException::new);

        log.info("문제: {}", joke.getQuestion());
        log.info("정답: {}", joke.getAnswer());

        joke.updateCalled();
        Joke saveJoke = jokeRepository.save(joke);
        log.info("isCalled: {}", joke.isCalled());
        assertThat(saveJoke.getId()).isEqualTo(testJoke.getId());
        assertThat(joke.getQuestion()).isEqualTo("개가 한 마리만 사는 나라는?");
        assertThat(joke.getAnswer()).isEqualTo("독일");
        assertThat(joke.isCalled()).isTrue();
    }

    @Test
    @Transactional
    void 아재개그상태리셋() {
        jokeRepository.resetCalledStatus();
        Joke joke = jokeRepository.findById(testJoke.getId()).orElseThrow(IllegalArgumentException::new);
        log.info("상태 확인: {}", joke.isCalled());
    }

    @Test
    @Transactional
    void false_호출() {
        List<Joke> byCalledFalse = jokeRepository.findByCalledFalseAndJokeType(JokeType.DEFAULT);
        System.out.println("byCalledFalse = " + byCalledFalse);

        for (int i = 0; i < byCalledFalse.size(); i++) {
            Joke joke = byCalledFalse.get(i);
            joke.updateCalled();
            jokeRepository.save(joke);
        }

        System.out.println("byCalledFalse = " + byCalledFalse);
    }

    @Test
    @Transactional
    void 문제_중복_검증() {
        String question = "노트북의 반대말은?";
        String answer = "노트남";
        Joke joke = Joke.builder()
                .question(question)
                .answer(answer)
                .build();
        List<Joke> findAll = jokeRepository.findAll();
        boolean b = findAll.stream().anyMatch(findJoke -> findJoke.getQuestion().equals(question));
        assertThrows(IllegalArgumentException.class, () -> {
            if (b) {
                throw new IllegalArgumentException("해당 문제는 이미 존재합니다.");
            } else {
                jokeRepository.save(joke);
            }
        });
    }

    @Test
    @Transactional
    @DisplayName("JokeType USER_ADDED 를 조회한다.")
    void 유저_개그() {
        List<Joke> byCalledFalseAndJokeType = jokeRepository.findByCalledFalseAndJokeType(JokeType.USER_ADDED);
        log.info("byCalledFalseAndJokeType = {} ",byCalledFalseAndJokeType);

    }
}