package org.pethack.lorby.services;

import jakarta.transaction.Transactional;
import org.pethack.lorby.model.User;
import org.pethack.lorby.model.UserImpDetails;
import org.pethack.lorby.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {


    public final UserRepository userRepository;
    private final EmailService emailService;


    public UserService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findUserByEmail(email).orElseThrow(() -> new UsernameNotFoundException(
                String.format("Email '%s' not found", email)
        ));
        return UserImpDetails.build(user);

    }

    public boolean checkConfirmationCode(User user, int enteredCode) {
        Duration duration = Duration.between(user.getCodeGenerationTime(), Instant.now());
        if (duration.toMinutes() >= 30) {
            return false;
        } else {
            return user.getConfirmationCode()==enteredCode;
        }
    }


}
