package ajae.uhtm.controller.bookmark;

import ajae.uhtm.dto.joke.JokeDto;
import ajae.uhtm.entity.Joke;
import ajae.uhtm.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @PostMapping("/api/v1/bookmark")
    public ResponseEntity<?> addBookmark(@RequestBody Joke joke,
                                                Principal principal) {
        log.info("joke: {}", joke);
        String name = principal.getName();
        log.info("username: {}", name);

        long result = bookmarkService.addBookmark(joke, name);

        if (result > 0) {
            return ResponseEntity.ok(Map.of("message", "북마크 등록되었습니다."));
        }

        return ResponseEntity.badRequest().body(Map.of("message", "북마크 등록에 실패했습니다."));
    }

    // 내 북마크 개그 가져오기
    @GetMapping("/api/v1/allJoke")
    public ResponseEntity<?> getBookmarkAllJokes(Principal principal) {
        String name = principal.getName();
        List<JokeDto> allJoke = bookmarkService.getAllJoke(name);

        if (allJoke.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "등록된 북마크가 없습니다."));
        }
        return ResponseEntity.ok(allJoke);
    }

    @PostMapping("/api/v1/check")
    public ResponseEntity<?> checkBookmark(@RequestBody JokeDto jokeDto,
                                           Principal principal) {
        String name = principal.getName();
        String question = jokeDto.toEntity().getQuestion();
        Boolean result = bookmarkService.checkBookmark(name, question);
        log.info("result: {}", result);
        return ResponseEntity.ok(result);
    }
}
