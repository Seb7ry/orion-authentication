package com.unibague.gradework.orionserver.service;

import com.unibague.gradework.orionserver.model.LoginRequest;
import org.springframework.http.ResponseEntity;

public interface IAuthService {
    ResponseEntity<?> authenticateUser(LoginRequest loginRequest);
}
