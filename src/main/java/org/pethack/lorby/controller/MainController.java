package org.pethack.lorby.controller;

import org.pethack.lorby.services.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/secured")
public class MainController {
    private final UserService userService;
    public MainController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user")
    public String userAccess(Principal principal){
        if(principal==null)
            return  null;
        return principal.getName();
    }

}
