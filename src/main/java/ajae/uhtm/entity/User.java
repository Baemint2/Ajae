package ajae.uhtm.entity;

import ajae.uhtm.dto.user.UserDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private long id;
    private String username;
    private String password;
    private String email;
    private String profile;
    private String nickname;
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider_type")
    private ProviderType providerType;

    @Column(name = "provider_key")
    private String providerKey;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @Column(name = "last_login_date")
    private LocalDateTime lastLogin;

    @OneToMany(mappedBy = "user")
    private List<Bookmark> bookmarks = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<UserJoke> userJokes = new ArrayList<>();

    public void changeLastLoginDate() {
        this.lastLogin = LocalDateTime.now();
    }

    @Builder
    public User(String username, String password, String email, String profile, String nickname, Role role, ProviderType providerType, String providerKey, boolean isDeleted, LocalDateTime lastLogin) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.profile = profile;
        this.nickname = nickname;
        this.role = role;
        this.providerType = providerType;
        this.providerKey = providerKey;
        this.isDeleted = isDeleted;
        this.lastLogin = lastLogin;
    }

    public UserDto toDto() {
        return UserDto.builder()
                .email(email)
                .nickname(nickname)
                .profile(profile)
                .providerKey(providerKey)
                .build();
    }


    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", profile='" + profile + '\'' +
                ", nickname='" + nickname + '\'' +
                ", role='" + role + '\'' +
                ", providerType=" + providerType +
                ", providerKey='" + providerKey + '\'' +
                ", isDeleted=" + isDeleted +
                ", lastLogin=" + lastLogin +
                '}';
    }
}
