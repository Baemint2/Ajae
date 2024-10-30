package ajae.uhtm.service;

import ajae.uhtm.entity.UserJoke;
import ajae.uhtm.repository.userJoke.UserJokeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserJokeService {

    private final UserJokeRepository userJokeRepository;

    @Transactional
    public long saveUserJoke(UserJoke userJoke) {
        return userJokeRepository.save(userJoke).getId();
    }
}
