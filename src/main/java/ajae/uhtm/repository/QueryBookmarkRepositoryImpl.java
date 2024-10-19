package ajae.uhtm.repository;

import ajae.uhtm.entity.Joke;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.util.List;

import static ajae.uhtm.entity.QBookmark.bookmark;
import static ajae.uhtm.entity.QJoke.joke;
import static ajae.uhtm.entity.QUser.user;

public class QueryBookmarkRepositoryImpl implements QueryBookmarkRepository {

    JPAQueryFactory queryFactory;

    public QueryBookmarkRepositoryImpl(EntityManager em) {
        queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Joke> getBookmarks(Long uerId) {
        List<Joke> jokeList = queryFactory.select(joke)
                .from(user)
                .leftJoin(bookmark)
                .on(user.id.eq(bookmark.user.id))
                .leftJoin(joke)
                .on(bookmark.joke.id.eq(joke.id))
                .where(user.id.eq(3L))
                .fetch();
        return jokeList;
    }
}
