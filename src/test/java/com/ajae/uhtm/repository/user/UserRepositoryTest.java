package com.ajae.uhtm.repository.user;

import com.ajae.uhtm.domain.user.ProviderType;
import com.ajae.uhtm.domain.user.User;
import com.ajae.uhtm.global.auth.oauth2.OauthResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("특정 유저를 조회한다.")
    void findByProviderKey() {
        // given
        OauthResponse oauthResponse = OauthResponse.create("testProviderId", "email@email.com", "profileImage", "테스트닉네임", ProviderType.KAKAO);
        User user = User.create(oauthResponse);

        userRepository.save(user);

        // when
        User findUser = userRepository.findByProviderKey("testProviderId").orElseThrow();

        // then
        assertThat(findUser.getNickname()).isEqualTo("테스트닉네임");
    }

}