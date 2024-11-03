package ajae.uhtm.service;

import ajae.uhtm.dto.UserJokeDto;
import ajae.uhtm.entity.JokeType;
import ajae.uhtm.entity.UserJoke;
import ajae.uhtm.repository.userJoke.UserJokeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public List<UserJokeDto> getAllUserJokes() {
        List<UserJoke> userJokes = userJokeRepository.selectAllUserJoke(JokeType.USER_ADDED);
        if(userJokes.isEmpty()) {
            throw new IllegalArgumentException("유저가 추가한 개그가 존재하지 않습니다.");
        }
        return userJokes.stream()
                .map(UserJoke::toDto)
                .toList();
    }

    @Transactional
    public UserJokeDto getUserJokeDetails(String jokeId, String userId) {
        long joke = Long.parseLong(jokeId);
        long user = Long.parseLong(userId);
        return userJokeRepository.selectUserJoke(joke, user).toDto();
    }
}
