package com.ajae.uhtm.global.auth;

import com.ajae.uhtm.domain.user.User;
import com.ajae.uhtm.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserSecurityService implements UserDetailsService {

    private final UserService userService;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.findByUsername(username);

        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole());
        List<GrantedAuthority> authorities = Collections.singletonList(authority);
        return new CustomUserDetails(user.toDto(), authorities);
    }
}
