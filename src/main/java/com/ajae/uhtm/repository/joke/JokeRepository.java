package com.ajae.uhtm.repository.joke;

import com.ajae.uhtm.domain.joke.Joke;
import com.ajae.uhtm.domain.joke.JokeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JokeRepository extends JpaRepository<Joke, Long>, QueryJokeRepository {

    @Modifying
    @Query("update Joke j SET j.called = false")
    void resetCalledStatus(); // 모든 아재개그 called false로 리셋

    List<Joke> findByCalledFalseAndJokeType(JokeType jokeType);

    Joke findByIdAndCalledFalse(long id);

    Joke findByQuestion(String question);
}
