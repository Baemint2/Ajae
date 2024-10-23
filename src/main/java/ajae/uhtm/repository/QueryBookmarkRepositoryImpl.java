package ajae.uhtm.repository;

import ajae.uhtm.entity.*;
import com.querydsl.core.types.dsl.BooleanExpression;
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
    public List<Joke> getBookmarks(Long userId) {
        return queryFactory.select(joke)
                .from(user)
                .leftJoin(bookmark)
                .on(user.id.eq(bookmark.user.id))
                .leftJoin(joke)
                .on(bookmark.joke.id.eq(joke.id))
                .where(user.id.eq(3L))
                .fetch();
    }

    @Override
    public Boolean checkBookmark(Long userId, Long jokeId) {
        return queryFactory.selectFrom(bookmark)
                .where(jokeEq(jokeId),
                        userEq(userId))
                .fetchOne() != null;
    }

    private BooleanExpression jokeEq(Long jokeId) {
        return joke.id != null ? joke.id.eq(jokeId) : null;
    }

    private BooleanExpression userEq(Long userId) {
        return user.id != null ? user.id.eq(userId) : null;
    }
}
