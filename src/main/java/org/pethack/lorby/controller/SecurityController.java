package org.pethack.lorby.controller;

import org.pethack.lorby.authRequest.SigninRequest;
import org.pethack.lorby.authRequest.SignupRequest;
import org.pethack.lorby.config.JwtCore;
import org.pethack.lorby.model.User;
import org.pethack.lorby.repository.UserRepository;
import org.pethack.lorby.services.EmailService;
import org.pethack.lorby.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/auth")
public class SecurityController {
    private UserService userService;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private JwtCore jwtCore;
    private EmailService emailService;

    @Autowired
    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }
    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
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


    @PostMapping("/signup")
    ResponseEntity<?> signup(@RequestBody SignupRequest signupRequest){
//        if(userRepository.existsByEmail(signupRequest.getEmail())){
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Выберите другой email");
//        }

        User user = new User();
        int confirmationCode = ThreadLocalRandom.current().nextInt(1000, 9999);
        user.setConfirmationCode(confirmationCode);
        user.setCodeGenerationTime(Instant.now());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));

        userRepository.save(user);
        emailService.sendConfirmationCode(user.getEmail(), confirmationCode);
        return ResponseEntity.ok("Успешно!");
    }
    @PostMapping("/signin")
    ResponseEntity<?> signin(@RequestBody SigninRequest signinRequest){
        Authentication authentication = null;
        try{
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signinRequest.getEmail(), signinRequest.getPassword()));
        }catch (BadCredentialsException e){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtCore.generateToken(authentication);
        return ResponseEntity.ok(jwt);
    }
    @PostMapping("/check-code")
    public boolean checkConfirmationCode(@RequestBody User user, @RequestParam int enteredCode) {
        return userService.checkConfirmationCode(user, enteredCode);
    }
}

