package com.ajae.uhtm.controller.user;

import com.ajae.uhtm.dto.user.UserDto;
import com.ajae.uhtm.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

import static com.ajae.uhtm.global.utils.CookieUtil.deleteCookie;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/api/v1/userInfo")
    public ResponseEntity<?> getUserInfo(Principal principal) {

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

    @PostMapping("/api/v1/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        deleteCookie(response, "accessToken");
        deleteCookie(response, "refreshToken");

        return ResponseEntity.ok("로그아웃 성공");

    }

}
