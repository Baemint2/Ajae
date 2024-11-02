package ajae.uhtm.controller.joke;

import ajae.uhtm.dto.UserJokeDto;
import ajae.uhtm.dto.joke.JokeDto;
import ajae.uhtm.service.JokeService;
import ajae.uhtm.service.UserJokeService;
import ajae.uhtm.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/api/v1/userJoke")
    public ResponseEntity<List<UserJokeDto>> getAllUserJoke() {
        List<UserJokeDto> allUserJokes = userJokeService.getAllUserJokes();
        return ResponseEntity.ok(allUserJokes);
    }


}
