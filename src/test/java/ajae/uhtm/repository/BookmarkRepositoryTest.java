package ajae.uhtm.repository;

import ajae.uhtm.entity.*;
import ajae.uhtm.repository.bookmakrk.BookmarkRepository;
import ajae.uhtm.repository.joke.JokeRepository;
import ajae.uhtm.repository.user.UserRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static ajae.uhtm.entity.QJoke.joke;
import static ajae.uhtm.entity.QUser.user;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class BookmarkRepositoryTest {

    @Autowired
    private BookmarkRepository bookmarkRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JokeRepository jokeRepository;

    JPAQueryFactory queryFactory;

    @Value("${provider-key.kakao}")
    String kakaoKey;

    @Value("${provider-key.naver}")
    String naverKey;

    @Autowired
    EntityManager em;

    User testUser;

    Joke testJoke;

    Bookmark testBookmark;

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

        User savedUser = userRepository.save(testUser);
        Joke savedJoke = jokeRepository.save(testJoke);

        testBookmark = Bookmark.builder()
                .user(savedUser)
                .joke(savedJoke)
                .build();

        bookmarkRepository.save(testBookmark);
    }

    @Test
    @Transactional
    void 북마크_추가() {
        User user = userRepository.findByProviderKey(testUser.getProviderKey())
                .orElseThrow(IllegalStateException::new);

        Joke joke = jokeRepository.findByQuestion("화를 제일 많이 내는 숫자는?");

        Bookmark bookmark = Bookmark.builder()
                .user(user)
                .joke(joke)
                .build();

        bookmarkRepository.save(bookmark);
    }

    @Test
    @Transactional
    void 북마크_제거() {
        String userId = kakaoKey;
        User user = userRepository.findByProviderKey(userId)
                            .orElseThrow();

        Joke joke = jokeRepository.findById(testBookmark.getJoke().getId())
                                .orElseThrow();

        Bookmark bookmark = Bookmark.builder()
                .user(user)
                .joke(joke)
                .build();

        bookmarkRepository.delete(bookmark);
    }

    @Test
    @Transactional
    void 북마크_체크_true() {
        User user = userRepository.findByProviderKey(testUser.getProviderKey())
                .orElseThrow(IllegalStateException::new);
        Joke joke = jokeRepository.findById(testBookmark.getJoke().getId()).orElseThrow();

        boolean bookmark1 = queryFactory.selectFrom(QBookmark.bookmark)
                .where(jokeEq(joke.getId()),
                        userEq(user.getId()))
                .fetchOne() != null;

        assertTrue(bookmark1);
    }

    @Test
    @Transactional
    void 북마크_체크_false() {
        Joke joke = jokeRepository.findById(testBookmark.getJoke().getId())
                .orElseThrow(IllegalStateException::new);

        boolean bookmark1 = queryFactory.selectFrom(QBookmark.bookmark)
                .where(jokeEq(joke.getId()),
                        userEq(-1L))
                .fetchOne() != null;

        assertFalse(bookmark1);
    }

    private BooleanExpression jokeEq(Long jokeId) {
        return joke.id != null ? joke.id.eq(jokeId) : null;
    }

    private BooleanExpression userEq(Long userId) {
        return user.id != null ? user.id.eq(userId) : null;
    }
}