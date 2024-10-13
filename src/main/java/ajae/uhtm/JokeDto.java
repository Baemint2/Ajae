package ajae.uhtm;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JokeDto {
    private String question;
    private String answer;

    public JokeDto(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    @Override
    public String toString() {
        return "JokeDto{" +
                "question='" + question + '\'' +
                ", answer='" + answer + '\'' +
                '}';
    }
}
