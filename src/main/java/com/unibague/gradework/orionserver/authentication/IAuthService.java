package com.unibague.gradework.orionserver.authentication;

import com.unibague.gradework.orionserver.user.models.UserLogDTO;


public interface IAuthService {

    UserLogDTO authenticate(LoginRequest loginRequest);
}
