package ajae.uhtm.repository.userJoke;

import ajae.uhtm.entity.JokeType;
import ajae.uhtm.entity.UserJoke;

import java.util.List;
import java.util.Optional;

public interface QueryUserJokeRepository {

    List<UserJoke> selectAllUserJoke(JokeType jokeType);

    UserJoke selectUserJoke(long jokeId, long userId);

}
