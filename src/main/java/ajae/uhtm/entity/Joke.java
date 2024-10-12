package ajae.uhtm.entity;

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

    public Joke(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }
}
