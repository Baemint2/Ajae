package ajae.uhtm.dto;

import ajae.uhtm.dto.joke.JokeDto;
import ajae.uhtm.dto.user.UserDto;
import ajae.uhtm.entity.UserJoke;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserJokeDto {

    private JokeDto joke;
    private UserDto user;

    public UserJoke toEntity() {
        return UserJoke.builder()
                .user(user.toEntity())
                .joke(joke.toEntity())
                .build();
    }
}
