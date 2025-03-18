package com.ajae.uhtm.repository.bookmark;

import com.ajae.uhtm.domain.joke.Joke;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.util.List;

import static com.ajae.uhtm.domain.bookmark.QBookmark.bookmark;
import static com.ajae.uhtm.domain.joke.QJoke.joke;
import static com.ajae.uhtm.domain.user.QUser.user;


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
                .where(user.id.eq(userId))
                .where(bookmark.isDeleted.eq(false))
                .fetch();
    }

    @Override
    public Boolean checkBookmark(Long userId, Long jokeId) {
        return queryFactory.selectFrom(bookmark)
                .where(jokeEq(jokeId),
                        userEq(userId))
                .fetchOne() != null;
    }

    @Override
    public Long getBookmark(Long userId, Long jokeId) {
        return queryFactory.select(bookmark.id)
                .from(bookmark)
                .where(jokeEq(jokeId),
                        userEq(userId))
                .fetchOne();
    }

    private BooleanExpression jokeEq(Long jokeId) {
        return joke.id != null ? joke.id.eq(jokeId) : null;
    }

    private BooleanExpression userEq(Long userId) {
        return user.id != null ? user.id.eq(userId) : null;
    }

    @Override
    public Long countBookmark(long userId) {
        return queryFactory.select(bookmark.count())
                .from(bookmark)
                .where(user.id.eq(userId), bookmark.isDeleted.eq(false))
                .fetchOne();
    }
}
