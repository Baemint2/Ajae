package ajae.uhtm;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDto {

    private String email;
    private String profile;
    private String nickname;
}
