package ajae.uhtm.service;

import ajae.uhtm.entity.Joke;
import ajae.uhtm.repository.BookmarkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;

    public List<Joke> getBookmarks(Long userId) {
        return bookmarkRepository.getBookmarks(userId);
    }
}
