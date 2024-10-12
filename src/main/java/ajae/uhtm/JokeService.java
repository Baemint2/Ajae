package ajae.uhtm;

import ajae.uhtm.entity.Joke;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class JokeService {

    private final JokeRepository jokeRepository;

    private final CsvReader csvReader;

    @Transactional
    public void importData(String filePath) {
        List<Joke> dataList = csvReader.readCsv(filePath);
        log.info("[importData] : {}", dataList.size());
        jokeRepository.saveAll(dataList);
    }
}
