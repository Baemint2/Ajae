package ajae.uhtm.dto;

import ajae.uhtm.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDto {

    private String email;
    private String profile;
    private String nickname;
    private String providerKey;

    public User toEntity() {
        return User.builder()
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
