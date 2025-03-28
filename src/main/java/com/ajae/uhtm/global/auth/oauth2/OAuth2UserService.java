package com.ajae.uhtm.global.auth.oauth2;

import com.ajae.uhtm.domain.user.ProviderType;
import com.ajae.uhtm.domain.user.Role;
import com.ajae.uhtm.service.UserService;
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

    public static final String EMAIL = "email";
    public static final String ID = "id";
    private final UserService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        // Role generate
        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_" + Role.USER);

        // nameAttributeKey
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();
        log.info("userNameAttributeName: {}", userNameAttributeName);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        switch (registrationId) {
            case "naver" -> {
                return naverOauth(oAuth2User, authorities);
            }
            case "kakao" -> kakaoOauth(oAuth2User);
            case "google" -> googleOauth(oAuth2User);
            default -> throw new IllegalStateException("Unexpected value: " + registrationId);
        }

        return new DefaultOAuth2User(authorities, oAuth2User.getAttributes(), userNameAttributeName);
    }

    private OAuth2User naverOauth(OAuth2User oAuth2User, List<GrantedAuthority> authorities) {
        Map<String, Object> response = oAuth2User.getAttribute("response");
        String providerId = (String) response.get(ID);
        String email = (String) response.get(EMAIL);
        String profile = (String) response.get("profile_image");
        String nickname = (String) response.get("name");
        OauthResponse oauthResponse = OauthResponse.create(providerId, email, profile, nickname, ProviderType.NAVER);
        userService.saveUserIfNotExist(oauthResponse);

        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
        attributes.put(ID, providerId);

        return new DefaultOAuth2User(authorities, attributes, ID);
    }

    private void kakaoOauth(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        Map<String, Object> properties = oAuth2User.getAttribute("properties");

        String providerId = attributes.get(ID).toString();
        String nickname = (String) properties.get("nickname");
        String profile = (String) properties.get("profile_image");

        Map<String, Object> kakaoAccount = oAuth2User.getAttribute("kakao_account");
        String email = (String) kakaoAccount.get(EMAIL);

        OauthResponse oauthResponse = OauthResponse.create(providerId, email, profile, nickname, ProviderType.KAKAO);

        userService.saveUserIfNotExist(oauthResponse);
    }

    private void googleOauth(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String providerId = (String) attributes.get("sub");
        String nickname = (String) attributes.get("name");
        String profile = (String) attributes.get("picture");
        String email = (String) attributes.get(EMAIL);

        OauthResponse oauthResponse = OauthResponse.create(providerId, email, profile, nickname, ProviderType.GOOGLE);
        userService.saveUserIfNotExist(oauthResponse);
    }


}
