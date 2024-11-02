package ajae.uhtm.repository.userJoke;

import ajae.uhtm.entity.UserJoke;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJokeRepository extends JpaRepository<UserJoke, Long>, QueryUserJokeRepository {

}
