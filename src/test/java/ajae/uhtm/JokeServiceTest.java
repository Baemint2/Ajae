package ajae.uhtm;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class JokeServiceTest {

    @Autowired
    private JokeService jokeService;
    @Value("${Ajae.zip.path}")
    private String AjaeZipPath;


    @Test
    void 데이터_삽입() {
        jokeService.importData(AjaeZipPath);
        log.info("");
    }
}