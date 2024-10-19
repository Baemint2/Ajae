package ajae.uhtm.repository;

import ajae.uhtm.entity.Bookmark;
import ajae.uhtm.entity.Joke;
import ajae.uhtm.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

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

    @Test
    @Commit
    void 북마크_추가() {
        Long userId = 3L;
        User user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        Joke joke = jokeRepository.findById(5L).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 개그번호입니다."));

        Bookmark bookmark = Bookmark.builder()
                .user(user)
                .joke(joke)
                .build();
        bookmarkRepository.save(bookmark);
    }
}