package ajae.uhtm.repository.userJoke;

import ajae.uhtm.entity.Joke;
import ajae.uhtm.entity.JokeType;
import ajae.uhtm.entity.UserJoke;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

import static ajae.uhtm.entity.QJoke.joke;
import static ajae.uhtm.entity.QUser.user;
import static ajae.uhtm.entity.QUserJoke.userJoke;

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
}
