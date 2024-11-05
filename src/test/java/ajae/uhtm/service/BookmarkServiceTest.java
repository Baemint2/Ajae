package ajae.uhtm.service;

import ajae.uhtm.dto.joke.JokeDto;
import ajae.uhtm.entity.Bookmark;
import ajae.uhtm.entity.Joke;
import ajae.uhtm.entity.JokeType;
import ajae.uhtm.entity.User;
import ajae.uhtm.repository.bookmark.BookmarkRepository;
import ajae.uhtm.repository.joke.JokeRepository;
import ajae.uhtm.repository.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import static org.mockito.Mockito.*;

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

    Bookmark testBookmark, testBookmark2;

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
        testJoke2.testJokeId(99998L);

        testBookmark = Bookmark.builder()
                .user(testUser)
                .joke(testJoke)
                .build();

        testBookmark2 = Bookmark.builder()
                .user(testUser)
                .joke(testJoke2)
                .build();

        testBookmark.testBookmarkId(1L);
        testBookmark2.testBookmarkId(2L);

        when(userService.findByUsername(testUser.getProviderKey())).thenReturn(testUser);
        when(jokeService.findByQuestion(anyString())).thenReturn(testJoke);
    }

    @Test
    void 북마크_호출() {
        when(bookmarkRepository.getBookmarks(anyLong())).thenReturn(List.of(testJoke, testJoke2));
        List<JokeDto> bookmarks = bookmarkService.getAllJoke(testBookmark.getUser().getProviderKey());
        assertThat(bookmarks).isNotNull();
        System.out.println("bookmarks: " + bookmarks);
    }

    @Test
    void 북마크_비어있음() {
        when(bookmarkRepository.getBookmarks(anyLong())).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> bookmarkService.getAllJoke(testBookmark.getUser().getProviderKey()))
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

    @Test
    @DisplayName("특정 북마크를 제거한다.")
    void deleteBookmark() {
        // 북마크 2개를 저장후, 사이즈 2 리스트를 반환한다.
        when(bookmarkRepository.getBookmarks(testUser.getId())).thenReturn(List.of(testJoke, testJoke2));

        when(bookmarkService.deleteBookmark(testUser.getProviderKey(), testJoke.getId())).thenReturn(1);

        // 북마크를 조회하여 사이즈가 2인지 조회, 이후 testJoke 삭제
        List<JokeDto> bookmarks = bookmarkService.getAllJoke(testUser.getProviderKey());
        assertThat(bookmarks.size()).isEqualTo(2);

        int i = bookmarkService.deleteBookmark(testUser.getProviderKey(), testJoke.getId());
        System.out.println("i = " + i);

        // testJoke를 삭제 후에 testJoke2만 남은 리스트 객체 생성 후 사이즈 체크
        when(bookmarkRepository.getBookmarks(testUser.getId())).thenReturn(List.of(testJoke2));
        bookmarks = bookmarkService.getAllJoke(testUser.getProviderKey());
        assertThat(bookmarks.size()).isEqualTo(1);

    }

}