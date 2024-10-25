package ajae.uhtm.service;

import ajae.uhtm.CsvReader;
import ajae.uhtm.dto.JokeDto;
import ajae.uhtm.entity.JokeType;
import ajae.uhtm.repository.JokeRepository;
import ajae.uhtm.entity.Joke;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

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

    @Transactional
    public JokeDto getRandomJoke() {
        // false 사이즈를 구해서 rand
        List<Joke> byCalledFalse = jokeRepository.findByCalledFalseAndJokeType(JokeType.DEFAULT);

        // called 가 전부 true 이면 동작
        if (byCalledFalse.isEmpty()) {
            log.info("called가 전부 true 입니다. 초기화 작업을 진행합니다.");
            jokeRepository.resetCalledStatus();
            byCalledFalse = jokeRepository.findByCalledFalseAndJokeType(JokeType.DEFAULT);
        }

        int size = byCalledFalse.size();
        int rand = new Random().nextInt(size);
        log.info("[getJoke] : {}", rand);
        Joke joke = byCalledFalse.get(rand);
        log.info("[getJoke] : {}", joke);
        joke.updateCalled();
        jokeRepository.save(joke);

        log.info("문제: {}", joke.getQuestion());
        log.info("정답: {}", joke.getAnswer());
        return joke.toDto();
    }

    @Transactional
    public Long saveJoke(Joke joke) {
        Joke joke2 = Joke.builder()
                .question(joke.getQuestion())
                .answer(joke.getAnswer())
                .jokeType(JokeType.USER_ADDED)
                .build();

        Joke save = jokeRepository.save(joke2);
        long id = save.getId();
        log.info("[saveJoke] : {}", id);
        return id;
    }

    @Transactional
    public Joke findByQuestion (String question) {
        return jokeRepository.findByQuestion(question);
    }

    @Transactional
    public Joke findById (Long id) {
        return jokeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 개그입니다."));
    }
}
