package com.ajae.uhtm.service;

import com.ajae.uhtm.domain.user.ProviderType;
import com.ajae.uhtm.domain.user.User;
import com.ajae.uhtm.global.auth.oauth2.OauthResponse;
import com.ajae.uhtm.repository.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
@SpringBootTest
class UserServiceTest {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Test
    @Transactional
    @DisplayName("Oauth2.0 유저를 조회한다.")
    void findOauth2User() {

        // given
        OauthResponse oauthResponse = OauthResponse.create("testProviderId", "email@email.com", "profileImage", "테스트닉네임", ProviderType.KAKAO);
        User user = User.create(oauthResponse);

        userRepository.save(user);

        // when
        User byUsername = userService.findByUsername("testProviderId");

        // then
        assertThat(byUsername.getProviderKey()).isEqualTo(user.getProviderKey());
    }

    @Test
    @Transactional
    @DisplayName("Oauth2.0 유저를 조회한다.")
    void findOauth2UserWithException() {

        // given
        OauthResponse oauthResponse = OauthResponse.create("testProviderId", "email@email.com", "profileImage", "테스트닉네임", ProviderType.KAKAO);
        User user = User.create(oauthResponse);

        userRepository.save(user);

        // when // then
        assertThatThrownBy(() -> userService.findByUsername("test"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 유저입니다.");
    }
}
