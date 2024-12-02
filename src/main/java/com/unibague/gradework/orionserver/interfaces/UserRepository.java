package com.unibague.gradework.orionserver.interfaces;

import com.unibague.gradework.orionserver.model.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findByEmail(String email);
}
