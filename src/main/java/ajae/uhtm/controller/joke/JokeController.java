package ajae.uhtm.controller.joke;

import ajae.uhtm.dto.UserJokeDto;
import ajae.uhtm.dto.joke.JokeDto;
import ajae.uhtm.service.JokeService;
import ajae.uhtm.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class JokeController {

    private final JokeService jokeService;

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

    @GetMapping("/joke")
    public String joke() {
        return "joke";
    }

    @GetMapping("/addJoke")
    public String addJoke() {
        return "addJoke";
    }

    @GetMapping("/anotherJoke")
    public String anotherJoke() {
        return "anotherJoke";
    }

    @GetMapping("/api/v1/userJoke")
    public ResponseEntity<List<UserJokeDto>> getAllUserJoke() {
        List<UserJokeDto> allUserJokes = jokeService.getAllUserJokes();
        return ResponseEntity.ok(allUserJokes);
    }


}
