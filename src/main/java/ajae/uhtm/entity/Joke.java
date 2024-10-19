package ajae.uhtm.entity;

import ajae.uhtm.dto.JokeDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "joke")
    private List<Bookmark> jokeList = new ArrayList<>();

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
