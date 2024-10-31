package ajae.uhtm.repository.joke;

import ajae.uhtm.entity.*;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.util.List;

import static ajae.uhtm.entity.QJoke.joke;
import static ajae.uhtm.entity.QUser.user;
import static ajae.uhtm.entity.QUserJoke.userJoke;

public class QueryJokeRepositoryImpl implements QueryJokeRepository {

    JPAQueryFactory queryFactory;

    public QueryJokeRepositoryImpl(EntityManager em) {
        queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Joke selectJokeById(long id) {
        return queryFactory
                .select(Projections.constructor(Joke.class, joke.question, joke.answer, joke.jokeType))
                .from(joke)
                .where(joke.id.eq(id))
                .fetchOne();
    }

    @Override
    public List<UserJoke> selectAllUserJoke(JokeType jokeType) {
        return queryFactory
                .selectFrom(userJoke)
                .where(joke.jokeType.eq(JokeType.USER_ADDED))
                .fetch();
    }

    private BooleanExpression jokeEq(Long jokeId) {
        return joke.id != null ? joke.id.eq(jokeId) : null;
    }

    private BooleanExpression userEq(Long userId) {
        return user.id != null ? user.id.eq(userId) : null;
    }
}
