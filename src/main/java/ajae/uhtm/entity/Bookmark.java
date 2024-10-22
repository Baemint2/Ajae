package ajae.uhtm.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Bookmark extends BaseTimeEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bookmark_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "joke_id")
    private Joke joke;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="user_id")
    private User user;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @Builder
    public Bookmark(Joke joke, User user, boolean isDeleted) {
        this.joke = joke;
        this.user = user;
        this.isDeleted = isDeleted;
    }

}
