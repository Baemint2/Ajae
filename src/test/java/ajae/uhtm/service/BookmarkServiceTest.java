package ajae.uhtm.service;

import ajae.uhtm.entity.Bookmark;
import ajae.uhtm.entity.Joke;
import ajae.uhtm.entity.User;
import ajae.uhtm.repository.BookmarkRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class BookmarkServiceTest {

    @Autowired
    BookmarkService bookmarkService;

    @Autowired
    UserService userService;

    @Autowired
    JokeService jokeService;

    @Value("${provider-key.kakao}")
    String kakaoKey;

    @Value("${provider-key.naver}")
    String naverKey;
    @Autowired
    private BookmarkRepository bookmarkRepository;

    @Test
    void 북마크_호출() {
        List<Joke> bookmarks = bookmarkService.getBookmarks(3L);
        assertThat(bookmarks).isNotNull();
        System.out.println("bookmarks = " + bookmarks);
    }

    @Test
    @Transactional
    void 북마크_저장() {
        User user = userService.findByUsername(kakaoKey);
        System.out.println("byUsername = " + user.toString());
        Joke question = jokeService.findByQuestion("화를 제일 많이 내는 숫자는?");

        long l = bookmarkService.addBookmark(question, user.getProviderKey());
        System.out.println("l = " + l);
    }

    @Test
    void 북마크_체크_존재() {
        Joke joke = jokeService.findById(143L);

        boolean bookmark = bookmarkService.checkBookmark(kakaoKey, joke.getQuestion());

        assertTrue(bookmark);
    }

    @Test
    void 북마크_체크_없음() {
        Joke joke = jokeService.findById(1L);

        boolean bookmark = bookmarkService.checkBookmark(kakaoKey, joke.getQuestion());

        assertFalse(bookmark);

    }

}