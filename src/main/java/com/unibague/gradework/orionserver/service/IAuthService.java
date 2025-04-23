package com.unibague.gradework.orionserver.service;

import com.unibague.gradework.orionserver.model.LoginRequest;
import com.unibague.gradework.orionserver.model.UserLogDTO;


public interface IAuthService {

    UserLogDTO authenticate(LoginRequest loginRequest);
}
