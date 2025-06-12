package com.unibague.gradework.orionserver.authentication;

import java.util.Map;


public interface IAuthService {

    Map<String, Object> authenticate(LoginRequest loginRequest);
}
