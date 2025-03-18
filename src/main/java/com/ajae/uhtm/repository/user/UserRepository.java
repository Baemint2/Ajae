package com.ajae.uhtm.repository.user;

import com.ajae.uhtm.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByProviderKey(String providerKey);

    Optional<User> findByUsername(String username);
}
