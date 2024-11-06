package ajae.uhtm.controller.bookmark;

import ajae.uhtm.dto.joke.JokeDto;
import ajae.uhtm.dto.joke.JokeRequestDto;
import ajae.uhtm.entity.Joke;
import ajae.uhtm.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public ResponseEntity<?> addBookmark(@RequestBody JokeDto jokeDto,
                                                Principal principal) {
        String name = principal.getName();
        long result = 0;
        Boolean checkBookmark = bookmarkService.checkBookmark(name, jokeDto.getId());

        /*
         * 북마크에 등록 되어있는지 체크 후 되어있다면 isDeleted -> false로 업데이트
         * 등록이 안 되어있으면 등록
         */

        if (checkBookmark) {
            result = bookmarkService.updateBookmark(name, jokeDto.getId());
        } else {
            result = bookmarkService.addBookmark(jokeDto.toEntity(), name);
        }

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

        if (principal == null) {
            return ResponseEntity.ok(false);
        }
        String name = principal.getName();
        long jokeId = jokeDto.getId();
        Boolean result = bookmarkService.checkBookmark(name, jokeId);

        log.info("result: {}", result);
        return ResponseEntity.ok(result);
    }

    // 북마크 제거
    @DeleteMapping("/api/v1/bookmark")
    public ResponseEntity<?> deleteBookmark(Principal principal,
                                            @RequestBody JokeDto requestDto) {
        String name = principal.getName();
        int result = bookmarkService.deleteBookmark(name, requestDto.getId());

        if(result > 0) {
            log.info("result: {}", result);
            return ResponseEntity.ok(Map.of("message", "북마크가 제거되었습니다."));
        }
        return ResponseEntity.badRequest().body(Map.of("message", "북마크가 제거에 실패했습니다."));
    }
}
