package ajae.uhtm.service;

import ajae.uhtm.entity.ProviderType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final UserService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("oAuth2User: {}", oAuth2User.toString());

        // Role generate
        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_USER");

        // nameAttributeKey
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        switch (userRequest.getClientRegistration().getRegistrationId()) {
            case "naver":
                return naverOauth(oAuth2User, authorities);
            case "kakao":
                kakaoOauth(oAuth2User);
                break;
            case "google":
                googleOauth(oAuth2User);
                break;
        }

        return new DefaultOAuth2User(authorities, oAuth2User.getAttributes(), userNameAttributeName);
    }

    private OAuth2User naverOauth(OAuth2User oAuth2User, List<GrantedAuthority> authorities) {
        Map<String, Object> response = oAuth2User.getAttribute("response");
        String providerId = (String) response.get("id");
        String email = (String) response.get("email");
        String profile = (String) response.get("profile_image");
        String nickname = (String) response.get("name");
        userService.saveUserIfNotExist(providerId, email, nickname, profile, ProviderType.NAVER);

        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
        attributes.put("id", providerId);

        return new DefaultOAuth2User(authorities, attributes, "id");
    }

    private void kakaoOauth(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String providerId = attributes.get("id").toString();
        Map<String, Object> properties = oAuth2User.getAttribute("properties");
        String nickname = (String) properties.get("nickname");
        String profile = (String) properties.get("profile_image");

        Map<String, Object> kakaoAccount = oAuth2User.getAttribute("kakao_account");
        String email = (String) kakaoAccount.get("email");
        userService.saveUserIfNotExist(providerId, email, nickname, profile, ProviderType.KAKAO);
    }

    private void googleOauth(OAuth2User oAuth2User) {
        log.info("googleOauth 호출");
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String providerId = (String) attributes.get("sub");
        String nickname = (String) attributes.get("name");
        String profile = (String) attributes.get("picture");
        String email = (String) attributes.get("email");

        userService.saveUserIfNotExist(providerId, email, nickname, profile, ProviderType.GOOGLE);
    }


}
