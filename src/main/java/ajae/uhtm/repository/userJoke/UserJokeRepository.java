package ajae.uhtm.repository.userJoke;

import ajae.uhtm.entity.JokeType;
import ajae.uhtm.entity.UserJoke;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJokeRepository extends JpaRepository<UserJoke, Long>, QueryUserJokeRepository {
    Page<UserJoke> findAllByJokeJokeType(JokeType jokeType, Pageable pageable);
}
