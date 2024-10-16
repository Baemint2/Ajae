package ajae.uhtm.entity;

import ajae.uhtm.JokeDto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"question", "answer", "called"})
public class Joke extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "joke_id")
    private long id;
    private String question;
    private String answer;
    private boolean called;

    public void updateCalled() {
        this.called = true;
    }

    @Builder
    public Joke(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public Joke(String question, String answer, boolean called) {
        this.question = question;
        this.answer = answer;
        this.called = called;
    }

    public JokeDto toDto() {
        return new JokeDto(this.question, this.answer);
    }
}
