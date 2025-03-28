package com.ajae.uhtm.domain.userJoke;

import com.ajae.uhtm.domain.joke.Joke;
import com.ajae.uhtm.domain.user.User;
import com.ajae.uhtm.dto.UserJokeDto;
import com.ajae.uhtm.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserJoke extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_joke_id")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "joke_id")
    private Joke joke;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public void testUserJokeId (long id) {
        this.id = id;
    }

    @Builder
    public UserJoke(Joke joke, User user) {
        this.joke = joke;
        this.user = user;
    }

    public static UserJoke create(Joke joke, User user) {
        return UserJoke.builder().joke(joke).user(user).build();
    }

    public UserJokeDto toDto() {
        return UserJokeDto.builder()
                .user(user.toInfoDto())
                .joke(joke.toDto())
                .build();
    }


}
