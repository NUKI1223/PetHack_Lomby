package org.pethack.lorby.controller;

import org.pethack.lorby.model.SigninRequest;
import org.pethack.lorby.model.SignupRequest;
import org.pethack.lorby.model.User;
import org.pethack.lorby.repository.UserRepository;
import org.pethack.lorby.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.util.Optional;


@Controller
@RequestMapping("/auth")
public class SecurityController {
    private UserService userService;
    private UserRepository userRepository;


    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }



    @GetMapping("/signUp")
    public String showRegistrationForm(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "signUp";
    }
    @PostMapping("/signUp")
    public String signUp(@ModelAttribute("user") SignupRequest signupRequest, Model model){

        if(userRepository.existsByEmail(signupRequest.getEmail())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Выберите другой email").toString();
        }

        userService.signUp(signupRequest);
        model.addAttribute("message", "Activate code");
        return "activate";
    }
    @PostMapping("/signin")
    ResponseEntity<?> signin(@RequestBody SigninRequest signinRequest){
        return userService.signIn(signinRequest);
    }
    @PostMapping("/check-code")
    public ResponseEntity<String> checkConfirmationCode(@RequestParam String email, @RequestParam int enteredCode) {
        Optional<User> optionalUser = userRepository.findUserByEmail(email);
        User user = optionalUser.get();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Пользователь с такой электронной почтой не найден.");
        }
        boolean isCodeValid = userService.checkConfirmationCode(user, enteredCode);
        if (isCodeValid) {
            user.setUserConfirmed(true); // пользователь теперь подтвержден
            userRepository.save(user);
            return ResponseEntity.ok("Регистрация успешно подтверждена!");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Недействительный код подтверждения или время действия кода истекло.");
        }
    }
}

