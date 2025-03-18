package com.ajae.uhtm.service;

import com.ajae.uhtm.dto.UserJokeDto;
import com.ajae.uhtm.dto.joke.JokeDto;
import com.ajae.uhtm.domain.joke.JokeType;
import com.ajae.uhtm.domain.userJoke.UserJoke;
import com.ajae.uhtm.repository.userjoke.UserJokeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserJokeService {

    private final UserJokeRepository userJokeRepository;

    @Transactional
    public UserJoke saveUserJoke(UserJoke userJoke) {
        return userJokeRepository.save(userJoke);
    }

    @Transactional
    public Page<UserJokeDto> getAllUserJokes(int pageNo) {
        PageRequest pageRequest = PageRequest.of(pageNo, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<UserJoke> userJokePage = userJokeRepository.findAllByJokeJokeType(JokeType.USER_ADDED, pageRequest);

        if(userJokePage.isEmpty()) {
            throw new IllegalArgumentException("유저가 추가한 개그가 존재하지 않습니다.");
        }
        return userJokePage.map(UserJoke::toDto);
    }

    @Transactional
    public UserJokeDto getUserJokeDetails(String jokeId, String userId) {
        long joke = Long.parseLong(jokeId);
        long user = Long.parseLong(userId);
        return userJokeRepository.selectUserJoke(joke, user).toDto();
    }

    @Transactional
    public List<JokeDto> findAllJokesByUserId(long userId) {
        List<UserJoke> userJokes = userJokeRepository.selectUserJokeById(userId);
        return userJokes.stream()
                .map(UserJoke::toDto)
                .map(UserJokeDto::getJoke)
                .toList();
    }

    @Transactional
    public Long countUserJoke(long userId) {
        return userJokeRepository.countUserJoke(userId);
    }

    @Transactional
    public Boolean existsUserJokeByUserId(Long userId, Long jokeId) {
        return userJokeRepository.existsUserJokeByUserId(userId, jokeId);
    }
}
