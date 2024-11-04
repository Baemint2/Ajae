package ajae.uhtm.dto.joke;

import ajae.uhtm.entity.Joke;
import ajae.uhtm.entity.JokeType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JokeDto {

    @JsonProperty(value = "jokeId")
    private long id;
    private String question;
    private String answer;

    @JsonIgnore
    private JokeType jokeType;

    public JokeDto(long id, String question, String answer, JokeType jokeType) {
        this.id = id;
        this.question = question;
        this.answer = answer;
        this.jokeType = jokeType;
    }

    public Joke toEntity() {
        return Joke.builder()
                .question(question)
                .answer(answer)
                .build();
    }

    @Override
    public String toString() {
        return "JokeDto{" +
                "question='" + question + '\'' +
                ", answer='" + answer + '\'' +
                '}';
    }
}
