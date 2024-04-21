package org.pethack.lorby.model;

import lombok.Data;

@Data
public class SigninRequest {
    private String email;
    private String password;
}
