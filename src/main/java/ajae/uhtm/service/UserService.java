package ajae.uhtm.service;

import ajae.uhtm.dto.JokeDto;
import ajae.uhtm.dto.UserDto;
import ajae.uhtm.entity.Joke;
import ajae.uhtm.entity.ProviderType;
import ajae.uhtm.entity.Role;
import ajae.uhtm.entity.User;
import ajae.uhtm.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final BookmarkService bookmarkService;

    public UserDto findByUsername(String username){
        User user = userRepository.findByProviderKey(username);
        return user.toDto();
    }

    @Transactional
    public User saveUserIfNotExist(String providerId, String email, String nickname, String profile, ProviderType providerType) {
        User existUser = userRepository.findByProviderKey(providerId);
        if(existUser == null) {
            User user = User.builder()
                    .email(email)
                    .nickname(nickname)
                    .profile(profile)
                    .providerKey(providerId)
                    .providerType(providerType)
                    .role(Role.USER)
                    .build();
            userRepository.save(user);
        }

        existUser.changeLastLoginDate();
        return existUser;
    }

    public List<JokeDto> getAllJoke(String providerId) {
        User user = userRepository.findByProviderKey(providerId);
        List<Joke> bookmarks = bookmarkService.getBookmarks(user.getId());

        return bookmarks.stream()
                .map(Joke::toDto)
                .toList();
    }
}
