package com.unibague.gradework.orionserver.service;

import com.unibague.gradework.orionserver.model.User;

import java.util.Optional;

public interface IUserService {
    User getUserByEmail(String email);
}
