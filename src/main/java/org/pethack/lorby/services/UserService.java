package org.pethack.lorby.services;

import org.pethack.lorby.config.JwtCore;
import org.pethack.lorby.model.SigninRequest;
import org.pethack.lorby.model.SignupRequest;
import org.pethack.lorby.model.User;
import org.pethack.lorby.model.UserImpDetails;
import org.pethack.lorby.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class UserService implements UserDetailsService {


    public UserRepository userRepository;
    private  EmailService emailService;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private JwtCore jwtCore;
    @Autowired
    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }
    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }
    @Autowired
    public void setJwtCore(JwtCore jwtCore) {
        this.jwtCore = jwtCore;
    }



    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findUserByEmail(email).orElseThrow(() -> new UsernameNotFoundException(
                String.format("Email '%s' not found", email)
        ));
        return UserImpDetails.build(user);

    }
    public ResponseEntity<?> signIn(SigninRequest signinRequest){
        Authentication authentication = null;
        Optional<User> optionalUser = userRepository.findUserByEmail(signinRequest.getEmail());
        User user = optionalUser.get();
        if (!user.getUserConfirmed()){
            userRepository.delete(user);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Вы не подтвердили свою электронную почту");
        }
        try{
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signinRequest.getEmail(), signinRequest.getPassword()));
        }catch (BadCredentialsException e){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtCore.generateToken(authentication);
        return ResponseEntity.ok(jwt);
    }
    public void signUp(SignupRequest signupRequest){
        User user = new User();
        int confirmationCode = ThreadLocalRandom.current().nextInt(1000, 9999);
        user.setConfirmationCode(confirmationCode);
        user.setUserConfirmed(false);
        user.setCodeGenerationTime(Instant.now());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        userRepository.save(user);
        emailService.sendConfirmationCode(user.getEmail(), confirmationCode);
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
