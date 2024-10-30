package ajae.uhtm.repository.joke;

import ajae.uhtm.entity.Joke;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import static ajae.uhtm.entity.QJoke.joke;

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
}
