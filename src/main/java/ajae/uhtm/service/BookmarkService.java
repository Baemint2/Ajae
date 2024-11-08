package ajae.uhtm.service;

import ajae.uhtm.dto.joke.JokeDto;
import ajae.uhtm.entity.Bookmark;
import ajae.uhtm.entity.Joke;
import ajae.uhtm.entity.User;
import ajae.uhtm.repository.bookmark.BookmarkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static ajae.uhtm.entity.QJoke.joke;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;

    private final JokeService jokeService;
    private final UserService userService;

    @Transactional
    public long addBookmark(JokeDto jokeDto, String username) {

        User user = userService.findByUsername(username);
        Joke joke = jokeService.findById(jokeDto.toEntity().getId());

        Bookmark bookmark = Bookmark.builder()
                .user(user)
                .joke(joke)
                .build();

        return bookmarkRepository.save(bookmark).getId();
    }

    public List<JokeDto> getAllJoke(String name) {
        User user = userService.findByUsername(name);
        List<Joke> bookmarks = bookmarkRepository.getBookmarks(user.getId());

        if(bookmarks == null || bookmarks.isEmpty()) {
            throw new IllegalArgumentException("북마크가 비어있습니다.");
        }

        return bookmarks.stream()
                .map(Joke::toDto)
                .toList();
    }

    @Transactional
    public Boolean checkBookmark(String username, long jokeId) {
        User user = userService.findByUsername(username);
        return bookmarkRepository.checkBookmark(user.getId(), jokeId);
    }

    @Transactional
    public int deleteBookmark(String username, long jokeId) {
        User user = userService.findByUsername(username);
        long bookmark = bookmarkRepository.getBookmark(user.getId(), jokeId);

        if(bookmark == 0) {
            throw new IllegalArgumentException("해당 북마크는 존재하지 않습니다.");
        }
        return bookmarkRepository.deleteBookmarkById(bookmark);
    }

    @Transactional
    public int updateBookmark(String username, long jokeId) {
        User user = userService.findByUsername(username);
        long bookmark = bookmarkRepository.getBookmark(user.getId(), jokeId);
        return bookmarkRepository.updateBookmarkById(bookmark);
    }

    @Transactional
    public Long countBookmark(long userId) {
        return bookmarkRepository.countBookmark(userId);
    }
}
