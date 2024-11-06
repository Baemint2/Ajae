package ajae.uhtm.controller.joke;

import ajae.uhtm.dto.UserJokeDto;
import ajae.uhtm.dto.UserJokeIdDto;
import ajae.uhtm.dto.joke.JokeDto;
import ajae.uhtm.entity.User;
import ajae.uhtm.service.JokeService;
import ajae.uhtm.service.UserJokeService;
import ajae.uhtm.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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
public class JokeController {

    private final JokeService jokeService;

    private final UserJokeService userJokeService;

    private final UserService userService;

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
    @GetMapping("/api/v1/allUserJoke")
    public ResponseEntity<?> getAllUserJoke(@RequestParam(required = false, defaultValue = "0", value = "page") int pageNo) {
        Page<UserJokeDto> allUserJokes = userJokeService.getAllUserJokes(pageNo);

        if(allUserJokes.isEmpty()){
            return ResponseEntity.badRequest().body(Map.of("message", "등록된 개그가 없습니다."));
        }
        return ResponseEntity.ok(allUserJokes);
    }

    // 특정 유저 개그 상세 보기
    @PostMapping("/api/v1/userJoke")
    public ResponseEntity<UserJokeDto> getJokeById(@RequestBody UserJokeIdDto requestDto){
        log.info("jokeId = {}", requestDto.getJokeId());
        log.info("userId = {}", requestDto.getUserId());
        UserJokeDto userJokeDetails = userJokeService.getUserJokeDetails(requestDto.getJokeId(), requestDto.getUserId());
        return ResponseEntity.ok(userJokeDetails);
    }

    // 특정 유저가 추가한 개그 리스트
    @GetMapping("/api/v1/userJoke")
    public ResponseEntity<List<JokeDto>> getUserJokes(Principal principal){
        User user = userService.findByUsername(principal.getName());
        List<JokeDto> jokesByUserId = userJokeService.findAllJokesByUserId(user.getId());

        return ResponseEntity.ok(jokesByUserId);
    }

}
