package ajae.uhtm.controller;

import ajae.uhtm.UserDto;
import ajae.uhtm.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

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
}
