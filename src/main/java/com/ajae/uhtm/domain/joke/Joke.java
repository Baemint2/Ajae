package com.ajae.uhtm.domain.joke;

import com.ajae.uhtm.domain.bookmark.Bookmark;
import com.ajae.uhtm.dto.joke.JokeDto;
import com.ajae.uhtm.dto.joke.JokeRequestDto;
import com.ajae.uhtm.domain.BaseTimeEntity;
import com.ajae.uhtm.domain.userJoke.UserJoke;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.time.LocalDateTime.now;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "question", "answer", "called"})
public class Joke extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "joke_id")
    private long id;
    private String question;
    private String answer;
    private boolean called;

    @Column(name = "joke_type")
    @Enumerated(EnumType.STRING)
    private JokeType jokeType = JokeType.DEFAULT;

    @OneToMany(mappedBy = "joke", cascade = CascadeType.ALL)
    private List<Bookmark> jokeList = new ArrayList<>();

    @OneToMany(mappedBy = "joke")
    private List<UserJoke> userJokeList = new ArrayList<>();

    public void updateCalled() {
        this.called = true;
    }

    public void testJokeId(long id) {
        this.id = id;
    }

    @Builder
    public Joke(String question, String answer, JokeType jokeType, List<UserJoke> userJokeList, LocalDateTime createdAt) {
        this.question = question;
        this.answer = answer;
        this.jokeType = jokeType;
        this.userJokeList = userJokeList;
    }

    public JokeDto toDto() {
        return new JokeDto(this.id, this.question, this.answer, this.jokeType, this.getCreatedAt());
    }

    public static Joke create(String question, String answer) {
        return Joke.builder()
                .question(question)
                .answer(answer)
                .jokeType(JokeType.DEFAULT)
                .createdAt(now())
                .build();
    }

    public JokeRequestDto toRequestDto() {
        return new JokeRequestDto(this.id, this.question, this.answer);
    }
}
