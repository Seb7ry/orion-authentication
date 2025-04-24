package com.unibague.gradework.orionserver.authentication;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
