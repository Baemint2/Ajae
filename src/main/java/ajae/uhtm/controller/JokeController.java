package ajae.uhtm.controller;

import ajae.uhtm.JokeDto;
import ajae.uhtm.service.JokeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

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

    @PostMapping("/api/v1/joke")
    public ResponseEntity<Map<String, String>> saveJoke(@RequestBody JokeDto jokeDto){
        Long id = jokeService.saveJoke(jokeDto);

        if(id > 0) return ResponseEntity.ok(Map.of("message", "문제 등록이 완료되었습니다."));
        else return ResponseEntity.ok(Map.of("message", "이미 존재하는 문제입니다."));
    }
}
