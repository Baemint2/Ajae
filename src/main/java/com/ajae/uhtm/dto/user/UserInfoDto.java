package com.ajae.uhtm.dto.user;

import com.ajae.uhtm.domain.user.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserInfoDto {

    private long id;
    private String profile;
    private String nickname;

    public User toEntity() {
        return User.builder()
                .profile(profile)
                .nickname(nickname)
                .build();
    }
}
