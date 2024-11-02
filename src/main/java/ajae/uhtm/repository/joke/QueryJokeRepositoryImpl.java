package ajae.uhtm.repository.joke;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import static ajae.uhtm.entity.QJoke.joke;
import static ajae.uhtm.entity.QUser.user;
import static ajae.uhtm.entity.QUserJoke.userJoke;

public class QueryJokeRepositoryImpl implements QueryJokeRepository {

    JPAQueryFactory queryFactory;

    public QueryJokeRepositoryImpl(EntityManager em) {
        queryFactory = new JPAQueryFactory(em);
    }

}
