package ajae.uhtm.controller.user;

import ajae.uhtm.dto.user.UserDto;
import ajae.uhtm.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/api/v1/userInfo")
    public ResponseEntity<?> getUserInfo(Principal principal) throws Exception {

        if(principal == null) {
            return ResponseEntity.ok().build();
        }

        UserDto byUsername = userService.findByUsername(principal.getName()).toDto();
        return ResponseEntity.ok(byUsername);
    }

    @GetMapping("/api/v1/loginCheck")
    public Boolean loginCheck(Principal principal) {

        if(principal == null) {
            return false;
        }

        UserDto user = userService.findByUsername(principal.getName()).toDto();
        log.info("loginCheck: {}", user != null);
        return user != null;
    }

}
