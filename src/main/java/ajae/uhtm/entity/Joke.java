package ajae.uhtm.entity;

import ajae.uhtm.dto.JokeDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

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

    @Column(name = "joke_type")
    @ColumnDefault("'DEFAULT'")
    @Enumerated(EnumType.STRING)
    private JokeType jokeType;

    @OneToMany(mappedBy = "joke")
    private List<Bookmark> jokeList = new ArrayList<>();

    public void updateCalled() {
        this.called = true;
    }

    @Builder
    public Joke(String question, String answer, JokeType jokeType) {
        this.question = question;
        this.answer = answer;
        this.jokeType = jokeType;
    }

    public JokeDto toDto() {
        return new JokeDto(this.question, this.answer, this.jokeType);
    }
}
