package ajae.uhtm.dto;

import ajae.uhtm.dto.joke.JokeDto;
import ajae.uhtm.dto.user.UserDto;
import ajae.uhtm.dto.user.UserInfoDto;
import ajae.uhtm.entity.UserJoke;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserJokeDto {

    private JokeDto joke;
    private UserInfoDto user;

    public UserJoke toEntity() {
        return UserJoke.builder()
                .user(user.toEntity())
                .joke(joke.toEntity())
                .build();
    }
}
