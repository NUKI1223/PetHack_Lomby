package org.pethack.lorby.controller;

import org.pethack.lorby.model.ActivationCodeForm;
import org.pethack.lorby.model.SigninRequest;
import org.pethack.lorby.model.SignupRequest;
import org.pethack.lorby.model.User;
import org.pethack.lorby.repository.UserRepository;
import org.pethack.lorby.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
        return "redirect:/activate";
    }
    @GetMapping("/signIn")
    public String showLoginForm(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "signIn";
    }
    @PostMapping("/signIn")
    public String signIn(@ModelAttribute("user") User user, BindingResult result, Model model){

        try {
            SigninRequest signinRequest = new SigninRequest();
            signinRequest.setEmail(user.getEmail());
            signinRequest.setPassword(user.getPassword());
            userService.signIn(signinRequest);
            // Successful login logic here
            return "redirect:/";
        } catch (BadCredentialsException e) {
            result.rejectValue("password", "error.user", "Invalid password.");
        } catch (DisabledException e) {
            result.rejectValue("email", "error.user", "User account is disabled.");
        }

        return "signIn";
    }

    @GetMapping("/activate")
    public String showActivationForm(Model model) {
        model.addAttribute("activationCode", new ActivationCodeForm());
        return "activate";
    }
    @PostMapping("/activate")
    public String checkConfirmationCode(@ModelAttribute("activationCode") ActivationCodeForm activationCodeForm, Model model, BindingResult result) {
        Optional<User> optionalUser = userRepository.findUsersByConfirmationCode(Integer.parseInt(activationCodeForm.getCode()));
        User user = optionalUser.get();
        if (user == null) {
            result.rejectValue("code", "error.activationCode", "Неверный код");
        }
        boolean isCodeValid = userService.checkConfirmationCode(user, Integer.parseInt(activationCodeForm.getCode()));
        if (isCodeValid) {
            user.setUserConfirmed(true); // пользователь теперь подтвержден
            userRepository.save(user);
            model.addAttribute("message", "Your account has been activated successfully. You can now log in.");
        } else {
            result.rejectValue("code", "error.activationCode", "Неверный код или истек срок его действия");
        }
        return "redirect:/signIn";
    }
}

