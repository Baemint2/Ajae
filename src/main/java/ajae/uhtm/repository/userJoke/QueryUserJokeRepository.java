package ajae.uhtm.repository.userJoke;

import ajae.uhtm.entity.JokeType;
import ajae.uhtm.entity.UserJoke;

import java.util.List;
import java.util.Optional;

public interface QueryUserJokeRepository {

    List<UserJoke> selectAllUserJoke(JokeType jokeType);

    UserJoke selectUserJoke(long jokeId, long userId);

    List<UserJoke> selectUserJokeById(long userId);

    Long countUserJoke(long userId);

    // 유저개그가 존재하는지 체크
    Boolean existsUserJokeByUserId(Long userId, Long jokeId);
}
