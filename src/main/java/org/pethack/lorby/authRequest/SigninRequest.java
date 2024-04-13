package org.pethack.lorby.authRequest;

import lombok.Data;

@Data
public class SigninRequest {
    private String email;
    private String password;
}
