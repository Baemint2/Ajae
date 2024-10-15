package ajae.uhtm;

import ajae.uhtm.service.JokeService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class AjaeDataInsertTest {

    @Autowired
    private JokeService jokeService;

    @Value("${Ajae.zip.path}")
    private String AjaeZipPath;


    @Test
    @Disabled
    void 데이터_삽입() {
        jokeService.importData(AjaeZipPath);
        log.info("");
    }
}
