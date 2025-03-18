package com.ajae.uhtm.repository.userjoke;

import com.ajae.uhtm.domain.joke.JokeType;
import com.ajae.uhtm.domain.userJoke.UserJoke;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.util.List;

import static com.ajae.uhtm.domain.joke.QJoke.joke;
import static com.ajae.uhtm.domain.user.QUser.user;
import static com.ajae.uhtm.domain.userJoke.QUserJoke.userJoke;


public class QueryUserJokeRepositoryImpl implements QueryUserJokeRepository {

    JPAQueryFactory queryFactory;

    public QueryUserJokeRepositoryImpl(EntityManager em) {
        queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<UserJoke> selectAllUserJoke(JokeType jokeType) {
        return queryFactory
                .selectFrom(userJoke)
                .where(joke.jokeType.eq(JokeType.USER_ADDED))
                .fetch();
    }

    @Override
    public UserJoke selectUserJoke(long jokeId, long userId) {
        return queryFactory
                .selectFrom(userJoke)
                .where(joke.id.eq(jokeId))
                .where(user.id.eq(userId))
                .fetchOne();
    }

    @Override
    public List<UserJoke> selectUserJokeById(long userId) {
        return queryFactory
                .selectFrom(userJoke)
                .where(user.id.eq(userId))
                .fetch();
    }

    @Override
    public Long countUserJoke(long userId) {
        return queryFactory.select(userJoke.count())
                .from(userJoke)
                .where(user.id.eq(userId))
                .fetchOne();
    }

    @Override
    public Boolean existsUserJokeByUserId(Long userId, Long jokeId) {
        return queryFactory
                .selectFrom(userJoke)
                .where(user.id.eq(userId))
                .where(joke.id.eq(jokeId))
                .fetchOne() != null;
    }
}
