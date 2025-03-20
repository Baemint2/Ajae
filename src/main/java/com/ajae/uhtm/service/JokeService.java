package com.ajae.uhtm.service;

import com.ajae.uhtm.global.utils.CsvReader;
import com.ajae.uhtm.dto.joke.JokeDto;
import com.ajae.uhtm.domain.joke.JokeType;
import com.ajae.uhtm.domain.user.User;
import com.ajae.uhtm.domain.userJoke.UserJoke;
import com.ajae.uhtm.repository.joke.JokeRepository;
import com.ajae.uhtm.domain.joke.Joke;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class JokeService {

    private final JokeRepository jokeRepository;

    private final UserService userService;

    private final UserJokeService userJokeService;

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
        Joke joke = byCalledFalse.get(rand);

        log.info("[getJoke] : {}", rand);
        log.info("[getJoke] : {}", joke);
        joke.updateCalled();
        jokeRepository.save(joke);

        log.info("문제: {}", joke.getQuestion());
        log.info("정답: {}", joke.getAnswer());
        return joke.toDto();
    }

    @Transactional
    public Joke saveJoke(Joke joke, String username) {
        Joke joke2 = Joke.builder()
                .question(joke.getQuestion())
                .answer(joke.getAnswer())
                .jokeType(JokeType.USER_ADDED)
                .build();

        Joke save = jokeRepository.save(joke2);
        User user = userService.findByUsername(username);

        if (user == null) {
            throw new IllegalArgumentException("User not found for username: " + username);
        }

        UserJoke userJoke = UserJoke.builder()
                .joke(save)
                .user(user)
                .build();
        userJokeService.saveUserJoke(userJoke);

        return save;
    }

    @Transactional
    public Joke findById (Long id) {
        return jokeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 개그입니다."));
    }

}
