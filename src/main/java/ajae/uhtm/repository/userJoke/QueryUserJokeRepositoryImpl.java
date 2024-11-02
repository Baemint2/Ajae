package ajae.uhtm.repository.userJoke;

import ajae.uhtm.entity.Joke;
import ajae.uhtm.entity.JokeType;
import ajae.uhtm.entity.UserJoke;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;

import static ajae.uhtm.entity.QJoke.joke;
import static ajae.uhtm.entity.QUserJoke.userJoke;

public class QueryUserJokeRepositoryImpl implements QueryUserJokeRepository {

    JPAQueryFactory queryFactory;

    @Override
    public List<UserJoke> selectAllUserJoke(JokeType jokeType) {
        return queryFactory
                .selectFrom(userJoke)
                .where(joke.jokeType.eq(JokeType.USER_ADDED))
                .fetch();
    }
}
