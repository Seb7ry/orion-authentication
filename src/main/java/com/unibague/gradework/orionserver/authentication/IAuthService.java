package com.unibague.gradework.orionauth.authentication;

import java.util.Map;


public interface IAuthService {

    Map<String, Object> authenticate(LoginRequest loginRequest);
    Map<String, Object> validateToken(String token);
    Map<String, Object> refreshAccessToken(String refreshToken);
}
