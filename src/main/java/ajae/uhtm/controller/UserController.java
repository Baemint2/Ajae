package ajae.uhtm.controller;

import ajae.uhtm.dto.JokeDto;
import ajae.uhtm.dto.UserDto;
import ajae.uhtm.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
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

        if (principal != null) {
            String name = principal.getName();
            UserDto byUsername = userService.findByUsername(name).toDto();
            return ResponseEntity.ok(byUsername);
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/api/v1/loginCheck")
    public Boolean loginCheck(Principal principal) {
        OAuth2User user = null;
        if (principal instanceof OAuth2AuthenticationToken oauthToken) {
            user = oauthToken.getPrincipal();
        }
        return user != null;
    }

}
