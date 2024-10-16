package ajae.uhtm.service;

import ajae.uhtm.UserDto;
import ajae.uhtm.entity.ProviderType;
import ajae.uhtm.entity.User;
import ajae.uhtm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserDto findByUsername(String username){
        User user = userRepository.findByProviderKey(username);
        return user.toDto();
    }

    public User saveUserIfNotExist(String providerId, String email, String nickname, String profile, ProviderType providerType) {
        User existUser = userRepository.findByProviderKey(providerId);
        if(existUser == null) {
            User user = User.builder()
                    .email(email)
                    .nickname(nickname)
                    .profile(profile)
                    .providerKey(providerId)
                    .providerType(providerType)
                    .build();
            userRepository.save(user);
        }
        return existUser;
    }
}
