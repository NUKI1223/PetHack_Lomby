package org.pethack.lorby.model;

import lombok.Data;

@Data
public class SignupRequest {
    private String email;
    private String password;
}
