package com.unibague.gradework.orionserver.service;

import com.unibague.gradework.orionserver.model.LoginRequest;
import com.unibague.gradework.orionserver.model.User;

import java.util.Optional;

public interface IAuthService {

    User authenticate(LoginRequest loginRequest);
}
