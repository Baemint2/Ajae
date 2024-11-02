package ajae.uhtm.repository.userJoke;

import ajae.uhtm.entity.JokeType;
import ajae.uhtm.entity.UserJoke;

import java.util.List;

public interface QueryUserJokeRepository {

    List<UserJoke> selectAllUserJoke(JokeType jokeType);

}
