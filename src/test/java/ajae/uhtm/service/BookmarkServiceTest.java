package ajae.uhtm.service;

import ajae.uhtm.entity.Bookmark;
import ajae.uhtm.entity.Joke;
import ajae.uhtm.entity.JokeType;
import ajae.uhtm.entity.User;
import ajae.uhtm.repository.bookmakrk.BookmarkRepository;
import ajae.uhtm.repository.joke.JokeRepository;
import ajae.uhtm.repository.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Slf4j
@SpringBootTest
class BookmarkServiceTest {

    @InjectMocks
    BookmarkService bookmarkService;

    @Mock
    UserService userService;

    @Mock
    JokeService jokeService;

    @Value("${provider-key.kakao}")
    String kakaoKey;

    @Value("${provider-key.naver}")
    String naverKey;

    @Mock
    private BookmarkRepository bookmarkRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JokeRepository jokeRepository;

    User testUser;

    Joke testJoke, testJoke2;

    Bookmark testBookmark;

    @BeforeEach
    void init() {
        testUser = User.builder()
                .providerKey("testProvider")
                .nickname("모지희")
                .build();

        testUser.testUserId(99999L);

        testJoke = Joke.builder()
                .question("개가 한 마리만 사는 나라는?")
                .answer("독일")
                .jokeType(JokeType.USER_ADDED)
                .build();

        testJoke2 = Joke.builder()
                .question("테스트 문제")
                .answer("테스트 정답")
                .jokeType(JokeType.USER_ADDED)
                .build();

        testJoke.testJokeId(99999L);
        testJoke.testJokeId(99998L);

        userRepository.save(testUser);
        jokeRepository.save(testJoke);

        testBookmark = Bookmark.builder()
                .user(testUser)
                .joke(testJoke)
                .build();

        testBookmark.testBookmarkId(99999L);
        bookmarkRepository.save(testBookmark);

        when(userService.findByUsername(testUser.getProviderKey())).thenReturn(testUser);
        when(jokeService.findByQuestion(anyString())).thenReturn(testJoke);
    }

    @Test
    void 북마크_호출() {
        when(bookmarkRepository.getBookmarks(anyLong())).thenReturn(List.of(testJoke, testJoke2));
        List<Joke> bookmarks = bookmarkService.getBookmarks(testBookmark.getUser().getId());
        assertThat(bookmarks).isNotNull();
        System.out.println("bookmarks: " + bookmarks);
    }

    @Test
    void 북마크_비어있음() {
        when(bookmarkRepository.getBookmarks(anyLong())).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> bookmarkService.getBookmarks(testBookmark.getUser().getId()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @Transactional
    void 북마크_저장() {

        when(bookmarkRepository.save(any(Bookmark.class))).thenReturn(testBookmark);

        long bookmark = bookmarkService.addBookmark(testJoke, testUser.getProviderKey());
        System.out.println("bookmark = " + bookmark);
    }

    @Test
    void 북마크_체크_존재() {
        when(bookmarkRepository.checkBookmark(testUser.getId(), testJoke.getId())).thenReturn(true);

        boolean bookmark = bookmarkService.checkBookmark(testUser.getProviderKey(), testJoke.getQuestion());

        assertTrue(bookmark);
    }

    @Test
    void 북마크_체크_없음() {
        when(bookmarkRepository.checkBookmark(testUser.getId(), testJoke.getId())).thenReturn(false);
        boolean bookmark = bookmarkService.checkBookmark(testUser.getProviderKey(), testJoke.getQuestion());

        assertFalse(bookmark);

    }

}