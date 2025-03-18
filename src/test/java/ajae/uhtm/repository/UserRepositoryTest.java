package ajae.uhtm.repository;

import ajae.uhtm.entity.*;
import com.ajae.uhtm.domain.joke.Joke;
import com.ajae.uhtm.repository.user.UserRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.ajae.uhtm.domain.bookmark.QBookmark.bookmark;
import static com.ajae.uhtm.domain.joke.QJoke.joke;
import static com.ajae.uhtm.domain.user.QUser.user;

@Slf4j
@SpringBootTest
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    JPAQueryFactory queryFactory;

    @Autowired
    EntityManager em;

    @BeforeEach
    public void init() {
        queryFactory = new JPAQueryFactory(em);
    }

    @Test
    void 북마크_가져오기() {
        List<Joke> joke1 = queryFactory.select(joke)
                .from(user)
                .leftJoin(bookmark)
                .on(user.id.eq(bookmark.user.id))
                .leftJoin(joke)
                .on(bookmark.joke.id.eq(joke.id))
                .where(user.id.eq(3L))
                .fetch();

        System.out.println("joke1 = " + joke1);
    }
}
