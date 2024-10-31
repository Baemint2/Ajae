package ajae.uhtm.repository.joke;

import ajae.uhtm.entity.Joke;
import ajae.uhtm.entity.JokeType;
import ajae.uhtm.entity.UserJoke;

import java.util.List;

public interface QueryJokeRepository {

   Joke selectJokeById(long id);

   List<UserJoke> selectAllUserJoke(JokeType jokeType);
}
