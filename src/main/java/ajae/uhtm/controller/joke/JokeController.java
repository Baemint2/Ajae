package ajae.uhtm.controller.joke;

import ajae.uhtm.dto.UserJokeDto;
import ajae.uhtm.dto.joke.JokeDto;
import ajae.uhtm.service.JokeService;
import ajae.uhtm.service.UserJokeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class JokeController {

    private final JokeService jokeService;

    private final UserJokeService userJokeService;

    @GetMapping("/api/v1/joke")
    public ResponseEntity<JokeDto> getJoke(){
        JokeDto joke = jokeService.getRandomJoke();
        return ResponseEntity.ok().body(joke);
    }

    @PostMapping("/api/v1/joke")
    public ResponseEntity<Map<String, String>> saveJoke(@RequestBody JokeDto jokeDto){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();
        jokeService.saveJoke(jokeDto.toEntity(), name);

        return ResponseEntity.ok(Map.of("message", "문제 등록이 완료되었습니다."));
    }

    // 유저개그 리스트를 조회한다.
    @GetMapping("/api/v1/userJoke")
    public ResponseEntity<?> getAllUserJoke() {
        List<UserJokeDto> allUserJokes = userJokeService.getAllUserJokes();

        if(allUserJokes.isEmpty()){
            return ResponseEntity.badRequest().body(Map.of("message", "등록된 개그가 없습니다."));
        }
        return ResponseEntity.ok(allUserJokes);
    }

    // 특정 유저 개그 상세 보기
    @GetMapping("/api/v1/joke/{id}")
    public ResponseEntity<UserJokeDto> getJokeById(@PathVariable("id") long id){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserJokeDto userJokeDetails = userJokeService.getUserJokeDetails(id, auth.getName());
        return ResponseEntity.ok(userJokeDetails);
    }


}
