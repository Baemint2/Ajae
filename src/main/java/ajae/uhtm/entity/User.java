package ajae.uhtm.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
    private String role;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider_type")
    private ProviderType providerType;

    @Column(name = "provider_key")
    private String providerKey;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @Column(name = "last_login_date")
    private LocalDateTime lastLogin;

    @Builder
    public User(String username, String password, String email, String profile, String nickname, String role, ProviderType providerType, String providerKey, boolean isDeleted, LocalDateTime lastLogin) {
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
