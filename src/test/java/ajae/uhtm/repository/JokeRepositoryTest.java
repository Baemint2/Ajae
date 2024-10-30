package ajae.uhtm.repository;

import ajae.uhtm.entity.Joke;
import ajae.uhtm.entity.JokeType;
import ajae.uhtm.repository.joke.JokeRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.util.Assert.isInstanceOf;

@Slf4j
@SpringBootTest
class JokeRepositoryTest {

    @Autowired
    private JokeRepository jokeRepository;

    JPAQueryFactory queryFactory;

    @Autowired
    EntityManager em;

    @BeforeEach
    public void init() {
        queryFactory = new JPAQueryFactory(em);
    }

    @Test
    @Transactional
    @Commit
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
    void 특정인덱스_체크_10번() {

        Joke joke = jokeRepository.findById(10L).get();

        log.info("문제: {}", joke.getQuestion());
        log.info("정답: {}", joke.getAnswer());

        joke.updateCalled();
        jokeRepository.save(joke);
        log.info("isCalled: {}", joke.isCalled());
        assertThat(joke.getQuestion()).isEqualTo("가장 지루한 중학교?");
        assertThat(joke.getAnswer()).isEqualTo("로딩중");
        assertThat(joke.isCalled()).isTrue();
    }

    @Test
    @Transactional
    @Commit
    void 아재개그상태리셋() {
        jokeRepository.resetCalledStatus();
        Joke joke = jokeRepository.findById(10L).get();
        log.info("상태 확인: {}", joke.isCalled());
    }

    @Test
    @Transactional
    @Commit
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
    void queryDSL테스트() {
        Joke joke = jokeRepository.selectJokeById(10L);
        log.info("joke: {}", joke.getId());
        log.info("joke: {}", joke.getQuestion());
        log.info("joke: {}", joke.getAnswer());
    }

    @Test
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
    void 유저_개그() {
        List<Joke> byCalledFalseAndJokeType = jokeRepository.findByCalledFalseAndJokeType(JokeType.USER_ADDED);
        log.info("byCalledFalseAndJokeType = {} ",byCalledFalseAndJokeType);
    }
}