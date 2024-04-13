package org.pethack.lorby.authRequest;

import lombok.Data;

@Data
public class SignupRequest {
    private String email;
    private String password;
}
