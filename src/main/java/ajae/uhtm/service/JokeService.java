package ajae.uhtm.service;

import ajae.uhtm.CsvReader;
import ajae.uhtm.JokeDto;
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
    public JokeDto getJoke() {
        // false 사이즈를 구해서 rand
        List<Joke> byCalledFalse = jokeRepository.findByCalledFalse();

        // called 가 전부 true 이면 동작
        if (byCalledFalse.isEmpty()) {
            log.info("called가 전부 true 입니다. 초기화 작업을 진행합니다.");
            jokeRepository.resetCalledStatus();
            byCalledFalse = jokeRepository.findByCalledFalse();
        }

        int size = byCalledFalse.size();
        long rand = new Random().nextLong(size) + 1;
        log.info("[getJoke] : {}", rand);
        Joke joke = jokeRepository.findByIdAndCalledFalse(rand);
        log.info("[getJoke] : {}", joke);
        joke.updateCalled();
        jokeRepository.save(joke);

        log.info("문제: {}", joke.getQuestion());
        log.info("정답: {}", joke.getAnswer());
        return joke.toDto();
    }

    @Transactional
    public Long saveJoke(JokeDto jokeDto) {
        Joke joke = Joke.builder()
                .question(jokeDto.getQuestion())
                .answer(jokeDto.getAnswer())
                .build();

        List<Joke> findAll = jokeRepository.findAll();
        boolean b = findAll.stream().anyMatch(findJoke -> findJoke.getQuestion().equals(jokeDto.getQuestion()));

        if(b) {
            log.info("이미 존재하는 문제입니다.");
            return 0L;
        } else {
            Joke save = jokeRepository.save(joke);
            long id = save.getId();
            log.info("[saveJoke] : {}", id);
            return id;
        }
    }
}
