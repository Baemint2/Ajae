package ajae.uhtm;

import ajae.uhtm.entity.Joke;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JokeRepository extends JpaRepository<Joke, Long> {
}
