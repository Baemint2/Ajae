package ajae.uhtm.service;

import ajae.uhtm.entity.User;
import ajae.uhtm.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.time.Instant;
import java.util.Optional;

import static ajae.uhtm.entity.QUser.user;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Slf4j
@SpringBootTest
public class UserServiceTest {

    @MockBean
    UserService userService;

    @MockBean
    UserRepository userRepository;

    @Autowired
    BookmarkService bookmarkService;

    @MockBean
    OAuth2UserService oAuth2UserService;

    @Mock
    private DefaultOAuth2UserService delegate;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    String clientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    String redirectUrl;
/*
    @Test
    void test() {
        OAuth2User oAuth2User = mock(OAuth2User.class);
        ClientRegistration clientRegistration = ClientRegistration.withRegistrationId("google")
                .clientId(clientId)
                .clientSecret(clientSecret)
                .redirectUri(redirectUrl)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .scope("profile", "email")
                .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth/oauthchooseaccount?client_id=" + clientId + "&redirect_uri=" + redirectUrl)
                .tokenUri("https://www.googleapis.com/oauth2/v4/token")
                .userInfoUri("https://www.googleapis.com/oauth2/v2/userinfo")
                .userNameAttributeName("sub")
                .clientName("Google")
                .build();

        OAuth2AccessToken accessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER,
                "access-token",
                Instant.now(),
                Instant.now().plusSeconds(3600));

        OAuth2UserRequest request = new OAuth2UserRequest(clientRegistration, accessToken);

        User user = mock(User.class);

        when(delegate.loadUser(any(OAuth2UserRequest.class))).thenReturn(oAuth2User);

        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        oAuth2UserService.loadUser(request);



    }
  */
}
