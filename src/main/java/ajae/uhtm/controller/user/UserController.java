package ajae.uhtm.controller.user;

import ajae.uhtm.dto.user.UserDto;
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
    public ResponseEntity<?> getUserInfo(Principal principal) throws Exception {
        String name = principal.getName();
        UserDto byUsername = userService.findByUsername(name).toDto();
        return ResponseEntity.ok(byUsername);
    }

    @GetMapping("/api/v1/loginCheck")
    public Boolean loginCheck(Principal principal) {
        String name = principal.getName();
        UserDto user = userService.findByUsername(name).toDto();
        return user != null;
    }

}
