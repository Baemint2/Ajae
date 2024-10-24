package ajae.uhtm.service;

import ajae.uhtm.dto.JokeDto;
import ajae.uhtm.entity.Bookmark;
import ajae.uhtm.entity.Joke;
import ajae.uhtm.entity.User;
import ajae.uhtm.repository.BookmarkRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;

    private final JokeService jokeService;
    private final UserService userService;

    public List<Joke> getBookmarks(Long userId) {
        return bookmarkRepository.getBookmarks(userId);
    }

    @Transactional
    public long addBookmark(Joke joke, String providerKey) {

        User byUsername = userService.findByUsername(providerKey);
        Joke byQuestion = jokeService.findByQuestion(joke.getQuestion());

        Bookmark bookmark = Bookmark.builder()
                .user(byUsername)
                .joke(byQuestion)
                .build();

        return bookmarkRepository.save(bookmark).getId();
    }

    public List<JokeDto> getAllJoke(String name) {
        User user = userService.findByUsername(name);
        List<Joke> bookmarks = getBookmarks(user.getId());

        return bookmarks.stream()
                .map(Joke::toDto)
                .toList();
    }

    public Boolean checkBookmark(String providerKey, String question) {
        User user = userService.findByUsername(providerKey);
        Joke joke = jokeService.findByQuestion(question);
        return bookmarkRepository.checkBookmark(user.getId(), joke.getId());
    }
}
