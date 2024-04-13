package org.pethack.lorby.controller;

import org.pethack.lorby.repository.UserRepository;
import org.pethack.lorby.services.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class SecurityController {
    private final UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    public SecurityController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @PostMapping("/signin")
    @PostMapping("/signup")
}
