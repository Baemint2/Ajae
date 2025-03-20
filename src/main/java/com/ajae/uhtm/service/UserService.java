package com.ajae.uhtm.service;

import com.ajae.uhtm.domain.user.User;
import com.ajae.uhtm.global.auth.oauth2.OauthResponse;
import com.ajae.uhtm.repository.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User findByUsername(String username){
        log.info("username: {}", username);
        return userRepository.findByProviderKey(username)
                .or(() -> userRepository.findByUsername(username))
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
    }

    @Transactional
    public void saveUserIfNotExist(OauthResponse oauthResponse) {
        String providerId = oauthResponse.getProviderId();
        log.info("[saveUserIfNotExist] providerId: {}", providerId);
        User existUser = userRepository.findByProviderKey(providerId)
                .orElseGet(() -> saveUser(oauthResponse));
        log.info("[saveUserIfNotExist] = {}", existUser);

        existUser.changeLastLoginDate();
    }

    private User saveUser(OauthResponse response) {
        User user = User.create(response);
        return userRepository.save(user);
    }


}
