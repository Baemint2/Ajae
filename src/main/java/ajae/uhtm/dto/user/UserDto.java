package ajae.uhtm.dto.user;

import ajae.uhtm.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDto {

    private long id;
    private String username;
    private String email;
    private String profile;
    private String nickname;
    private String password;
    private String providerKey;
    private String accessToken;

    public User toEntity() {
        return User.builder()
                .username(username)
                .email(email)
                .nickname(nickname)
                .profile(profile)
                .providerKey(providerKey)
                .build();
    }

    @Override
    public String toString() {
        return "UserDto{" +
                "email='" + email + '\'' +
                ", profile='" + profile + '\'' +
                ", nickname='" + nickname + '\'' +
                '}';
    }
}
