package ajae.uhtm.repository;

import com.ajae.uhtm.repository.joke.JokeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
public class JokeRepositoryTest {

    @Autowired
    JokeRepository jokeRepository;
}
