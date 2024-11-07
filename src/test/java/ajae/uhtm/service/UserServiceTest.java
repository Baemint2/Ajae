package ajae.uhtm.service;

import ajae.uhtm.auth.oauth2.OAuth2UserService;
import ajae.uhtm.entity.User;
import ajae.uhtm.repository.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Slf4j
@SpringBootTest
public class UserServiceTest {

    @InjectMocks
    UserService userService;

    @Mock
    UserRepository userRepository;

    User testUser, testUser2;

    @BeforeEach
    void init() {
        testUser = User.builder()
                .providerKey("moz1mozi")
                .nickname("모지희")
                .build();

        testUser.testUserId(1L);

        testUser2 = User.builder()
                .username("test123")
                .nickname("tester1")
                .build();

        testUser.testUserId(2L);

    }

    @Test
    @Transactional
    @DisplayName("일반 가입 유저를 조회한다.")
    void findOriginUser() {
        when(userRepository.findByUsername(testUser2.getUsername())).thenReturn(Optional.of(testUser2));

        User byUsername = userService.findByUsername(testUser2.getUsername());
        log.info(byUsername.toString());
    }

    @Test
    @Transactional
    @DisplayName("Oauth2.0 유저를 조회한다.")
    void findOauth2User() {
        when(userRepository.findByProviderKey(testUser.getProviderKey())).thenReturn(Optional.of(testUser));

        User byUsername = userService.findByUsername(testUser.getProviderKey());
        log.info(byUsername.toString());
    }

    @Test
    @Transactional
    @DisplayName("유저 조회 실패")
    void findUser_fail() {
        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.findByUsername(testUser.getUsername()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 유저입니다.");
    }
}
