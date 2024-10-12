package ajae.uhtm.repository;

import ajae.uhtm.entity.Joke;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface JokeRepository extends JpaRepository<Joke, Long>, QueryJokeRepository {

    @Modifying
    @Query("update Joke j SET j.called = false")
    void resetCalledStatus(); // 모든 아재개그 called false로 리셋

    List<Joke> findByCalledFalse();

    Joke findByIdAndCalledFalse(long id);
}
