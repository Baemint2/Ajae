package ajae.uhtm.service;

import com.ajae.uhtm.domain.joke.Joke;
import com.ajae.uhtm.domain.joke.JokeType;
import com.ajae.uhtm.domain.user.User;
import com.ajae.uhtm.dto.joke.JokeDto;
import com.ajae.uhtm.domain.bookmark.Bookmark;
import com.ajae.uhtm.repository.bookmark.BookmarkRepository;
import com.ajae.uhtm.repository.joke.JokeRepository;
import com.ajae.uhtm.repository.user.UserRepository;
import com.ajae.uhtm.service.BookmarkService;
import com.ajae.uhtm.service.JokeService;
import com.ajae.uhtm.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

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

    @Mock
    private BookmarkRepository bookmarkRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JokeRepository jokeRepository;

    User testUser, testUser2;

    Joke testJoke, testJoke2;

    Bookmark testBookmark, testBookmark2;

    @BeforeEach
    void init() {
        testUser = User.builder()
                .providerKey("moz1mozi")
                .nickname("모지희")
                .build();

        testUser.testUserId(1L);

        testUser2 = User.builder()
                .username("test123")
                .nickname("tester1")
                .build();

        testUser.testUserId(2L);

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
                .user(testUser2)
                .joke(testJoke2)
                .build();

        testBookmark.testBookmarkId(1L);
        testBookmark2.testBookmarkId(2L);

        when(userService.findByUsername(testUser.getProviderKey())).thenReturn(testUser);
        when(jokeService.findById(anyLong())).thenReturn(testJoke);
    }

    @Test
    @Transactional
    @DisplayName("특정 유저의 북마크 리스트를 조회한다.")
    void 북마크_호출() {
        when(bookmarkRepository.getBookmarks(anyLong())).thenReturn(List.of(testJoke, testJoke2));
        List<JokeDto> bookmarks = bookmarkService.getAllJoke(testBookmark.getUser().getProviderKey());
        assertThat(bookmarks).isNotNull();
        System.out.println("bookmarks: " + bookmarks);
    }

    @Test
    @Transactional
    @DisplayName("특정 유저의 북마크 리스트가 비어 있을 때 예외를 발생시킨다.")
    void 북마크_비어있음() {
        when(bookmarkRepository.getBookmarks(anyLong())).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> bookmarkService.getAllJoke(testBookmark.getUser().getProviderKey()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @Transactional
    @DisplayName("특정 유저가 특정 개그를 자신의 북마크에 등록한다.")
    void 북마크_저장() {

        when(bookmarkRepository.save(any(Bookmark.class))).thenReturn(testBookmark);

        long bookmark = bookmarkService.addBookmark(testJoke.toDto(), testUser.getProviderKey());
        System.out.println("bookmark = " + bookmark);
    }

    @Test
    @Transactional
    @DisplayName("현재 개그가 북마크에 존재하여 true를 반환한다.")
    void 북마크_체크_존재() {
        when(bookmarkRepository.checkBookmark(testUser.getId(), testJoke.getId())).thenReturn(true);

        boolean bookmark = bookmarkService.checkBookmark(testUser.getProviderKey(), testJoke.getId());

        assertTrue(bookmark);
    }

    @Test
    @Transactional
    @DisplayName("현재 개그가 북마크에 존재하지 않아 false를 반환한다.")
    void 북마크_체크_없음() {
        when(bookmarkRepository.checkBookmark(testUser.getId(), testJoke.getId())).thenReturn(false);
        boolean bookmark = bookmarkService.checkBookmark(testUser.getProviderKey(), testJoke.getId());

        assertFalse(bookmark);

    }

    @Test
    @Transactional
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

    @Test
    @Transactional
    @DisplayName("특정 북마크 제거에 실패한다. (이미 삭제하여 해당 북마크가 존재하지 않을때)")
    void deleteBookmarkFail() {
        when(bookmarkRepository.getBookmarks(testUser.getId())).thenReturn(List.of(testJoke));
        when(bookmarkRepository.getBookmark(testUser.getId(), testJoke.getId())).thenReturn(1L);
        when(bookmarkService.deleteBookmark(testUser.getProviderKey(), testJoke.getId())).thenReturn(1);

        // 북마크를 조회하여 사이즈가 2인지 조회, 이후 testJoke 삭제
        List<JokeDto> bookmarks = bookmarkService.getAllJoke(testUser.getProviderKey());
        assertThat(bookmarks.size()).isEqualTo(1);

        int i = bookmarkService.deleteBookmark(testUser.getProviderKey(), testJoke.getId());
        System.out.println("i = " + i);

        when(bookmarkRepository.getBookmarks(testUser.getId())).thenReturn(List.of(testJoke2));
        when(bookmarkRepository.getBookmark(testUser.getId(), testJoke.getId())).thenReturn(0L);
        bookmarks = bookmarkService.getAllJoke(testUser.getProviderKey());
        assertThatThrownBy(() -> bookmarkService.deleteBookmark(testUser.getProviderKey(), testJoke.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 북마크는 존재하지 않습니다.");
    }

    @Test
    @Transactional
    @DisplayName("특정 유저가 등록한 북마크의 개수를 조회한다")
    void getBookmarkCountById() {
        when(bookmarkRepository.countBookmark(testUser.getId())).thenReturn(2L);
        Long bookmarkCount = bookmarkService.countBookmark(testUser.getId());
        assertThat(bookmarkCount).isEqualTo(2L);
    }

}