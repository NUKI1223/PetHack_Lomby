package org.pethack.lorby.repository;

import org.pethack.lorby.model.ActivationCodeForm;
import org.pethack.lorby.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
 Optional<User> findUserByEmail(String email);
 Optional<User> findUsersByConfirmationCode(int confirmationCode);
 Boolean existsByEmail(String email);
}
