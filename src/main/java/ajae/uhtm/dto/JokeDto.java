package ajae.uhtm.dto;

import ajae.uhtm.entity.Joke;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JokeDto {
    private String question;
    private String answer;

    public JokeDto(String question, String answer) {
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
        return "JokeDto{" +
                "question='" + question + '\'' +
                ", answer='" + answer + '\'' +
                '}';
    }
}
