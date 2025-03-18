package com.ajae.uhtm.dto.joke;


import com.ajae.uhtm.domain.joke.Joke;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class JokeRequestDto {

    @JsonProperty("jokeId")
    private long id;
    private String question;
    private String answer;

    public JokeRequestDto() {
    }

    @JsonCreator
    public JokeRequestDto(Long id,
                          String question,
                          String answer) {
        this.id = id;
        this.question = question;
        this.answer = answer;
    }

    public Joke toEntity() {
        return Joke.builder()
                .question(question)
                .answer(answer)
                .build();
    }

    @Override
    public String toString() {
        return "JokeRequestDto{" +
                "jokeId='" + id + '\'' +
                ", question='" + question + '\'' +
                ", answer='" + answer + '\'' +
                '}';
    }
}
