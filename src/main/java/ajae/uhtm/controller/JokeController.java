package ajae.uhtm.controller;

import ajae.uhtm.JokeDto;
import ajae.uhtm.service.JokeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class JokeController {

    private final JokeService jokeService;

    @GetMapping("/api/v1/joke")
    public ResponseEntity<JokeDto> getJoke(){
        JokeDto joke = jokeService.getJoke();
        return ResponseEntity.ok().body(joke);
    }

    @GetMapping("joke")
    public String joke() {
        return "joke";
    }
}
