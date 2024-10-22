package ajae.uhtm.repository;

import ajae.uhtm.entity.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static ajae.uhtm.entity.QBookmark.bookmark;
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

    @BeforeEach
    public void init() {
        queryFactory = new JPAQueryFactory(em);
    }

    @Test
    @Transactional
    void 북마크_추가() {
        String userId = kakaoKey;
        User user = userRepository.findByProviderKey(userId)
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

        Joke joke = jokeRepository.findById(143L)
                                .orElseThrow();

        Bookmark bookmark = Bookmark.builder()
                .user(user)
                .joke(joke)
                .build();

        bookmarkRepository.delete(bookmark);
    }

    @Test
    void 북마크_체크() {
        String providerKey = naverKey;
        User user = userRepository.findByProviderKey(providerKey)
                .orElseThrow(IllegalStateException::new);
        Joke joke = jokeRepository.findById(72L).orElseThrow();

        Bookmark bookmark = Bookmark.builder()
                .user(user)
                .joke(joke)
                .build();
        System.out.println("bookmark = " + bookmark.getUser());
        Bookmark bookmark1 = queryFactory.selectFrom(QBookmark.bookmark)
                .where(QJoke.joke.id.eq(joke.getId()),
                        QUser.user.id.eq(user.getId()))
                .fetchOne();
        System.out.println("bookmark1 = " + bookmark1);


    }
}