package ajae.uhtm.repository;

import ajae.uhtm.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByProviderKey(String providerKey);
}
