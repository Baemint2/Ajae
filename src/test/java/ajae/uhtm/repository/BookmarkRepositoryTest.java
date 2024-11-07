package ajae.uhtm.repository;

import ajae.uhtm.entity.*;
import ajae.uhtm.repository.bookmark.BookmarkRepository;
import ajae.uhtm.repository.joke.JokeRepository;
import ajae.uhtm.repository.user.UserRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static ajae.uhtm.entity.QBookmark.bookmark;
import static ajae.uhtm.entity.QJoke.joke;
import static ajae.uhtm.entity.QUser.user;
import static ajae.uhtm.entity.QUserJoke.userJoke;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@Slf4j
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookmarkRepositoryTest {

    @Autowired
    private BookmarkRepository bookmarkRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JokeRepository jokeRepository;

    JPAQueryFactory queryFactory;

    @Autowired
    EntityManager em;

    User testUser;

    Joke testJoke, testJoke2;

    Bookmark testBookmark, testBookmark2;

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

        User savedUser = userRepository.save(testUser);
        Joke savedJoke = jokeRepository.save(testJoke);
        Joke savedJoke2 = jokeRepository.save(testJoke2);

        testBookmark = Bookmark.builder()
                .user(savedUser)
                .joke(savedJoke)
                .build();

        testBookmark2 = Bookmark.builder()
                .user(savedUser)
                .joke(savedJoke2)
                .build();

        bookmarkRepository.save(testBookmark);
        bookmarkRepository.save(testBookmark2);
    }

    @Test
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
    @DisplayName("특정 북마크를 제거한다.")
    void 북마크_제거() {
        List<Joke> bookmarks = bookmarkRepository.getBookmarks(testUser.getId());
        assertThat(bookmarks.size()).isEqualTo(2);
        int i = bookmarkRepository.deleteBookmarkById(testBookmark.getId());
        bookmarks = bookmarkRepository.getBookmarks(testUser.getId());
        for (Joke bookmark : bookmarks) {
            System.out.println("bookmark = " + bookmark.toString());
        }
        assertThat(bookmarks.size()).isEqualTo(1);
    }

    @Test
    void 북마크_체크_true() {
        User user = userRepository.findByProviderKey(testUser.getProviderKey())
                .orElseThrow(IllegalStateException::new);
        Joke joke = jokeRepository.findById(testBookmark.getJoke().getId()).orElseThrow();

        boolean bookmark1 = queryFactory.selectFrom(bookmark)
                .where(jokeEq(joke.getId()),
                        userEq(user.getId()))
                .fetchOne() != null;

        assertTrue(bookmark1);
    }

    @Test
    void 북마크_체크_false() {
        Joke joke = jokeRepository.findById(testBookmark.getJoke().getId())
                .orElseThrow(IllegalStateException::new);

        boolean bookmark1 = queryFactory.selectFrom(bookmark)
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

    @Test
    @DisplayName("북마크가 이미 테이블에 존재하면 isDeleted를 false로 업데이트한다.")
    void updatedBookmark() {
        List<Joke> bookmarks = bookmarkRepository.getBookmarks(testUser.getId());
        int i = 0;
        i = bookmarkRepository.deleteBookmarkById(testBookmark.getId());
        bookmarks = bookmarkRepository.getBookmarks(testUser.getId());
        for (Joke bookmark : bookmarks) {
            System.out.println("bookmark = " + bookmark.toString());
        }

        i = bookmarkRepository.updateBookmarkById(testBookmark.getId());
        bookmarks = bookmarkRepository.getBookmarks(testUser.getId());
        for (Joke bookmark : bookmarks) {
            System.out.println("bookmark = " + bookmark.toString());
        }
    }

    @Test
    @DisplayName("특정 유저가 등록한 북마크의 개수를 조회한다")
    void getUserJokeCountById() {
        Long l = queryFactory.select(bookmark.count())
                .from(bookmark)
                .where(user.id.eq(testUser.getId()))
                .fetchOne();

        log.info("bookmarkCount = {}", l);
    }
}