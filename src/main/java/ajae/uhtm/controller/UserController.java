package ajae.uhtm.controller;

import ajae.uhtm.dto.JokeDto;
import ajae.uhtm.dto.UserDto;
import ajae.uhtm.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/api/v1/userInfo")
    public ResponseEntity<?> getUserInfo(Principal principal) {
        String name = principal.getName();
        UserDto byUsername = userService.findByUsername(name);
        return ResponseEntity.ok(byUsername);
    }

    @GetMapping("/api/v1/allJoke")
    public ResponseEntity<?> getAllJokes(Principal principal) {
        String name = principal.getName();
        List<JokeDto> allJoke = userService.getAllJoke(name);
        return ResponseEntity.ok(allJoke);
    }
}
