package ajae.uhtm.repository.joke;

import ajae.uhtm.entity.Joke;

public interface QueryJokeRepository {

    Joke selectJokeById(long id);
}
