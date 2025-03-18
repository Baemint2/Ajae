package com.ajae.uhtm.repository.userjoke;

import com.ajae.uhtm.domain.joke.JokeType;
import com.ajae.uhtm.domain.userJoke.UserJoke;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserJokeRepository extends JpaRepository<UserJoke, Long>, QueryUserJokeRepository {
    Page<UserJoke> findAllByJokeJokeType(JokeType jokeType, Pageable pageable);
}
