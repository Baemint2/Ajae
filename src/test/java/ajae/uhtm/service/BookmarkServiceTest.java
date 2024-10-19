package ajae.uhtm.service;

import ajae.uhtm.entity.Joke;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class BookmarkServiceTest {

    @Autowired
    BookmarkService bookmarkService;

    @Test
    void 북마크_호출() {
        List<Joke> bookmarks = bookmarkService.getBookmarks(3L);
        assertThat(bookmarks).isNotNull();
        System.out.println("bookmarks = " + bookmarks);
    }

}