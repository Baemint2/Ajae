package ajae.uhtm.entity;

import ajae.uhtm.JokeDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
