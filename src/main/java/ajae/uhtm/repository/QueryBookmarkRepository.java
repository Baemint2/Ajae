package ajae.uhtm.repository;

import ajae.uhtm.entity.Joke;

import java.util.List;

public interface QueryBookmarkRepository {

    List<Joke> getBookmarks(Long uerId);
}
