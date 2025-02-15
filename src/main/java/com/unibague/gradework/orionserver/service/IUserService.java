package com.unibague.gradework.orionserver.service;

import com.unibague.gradework.orionserver.model.ProgramDTO;
import com.unibague.gradework.orionserver.model.User;

import java.util.List;
import java.util.Optional;

public interface IUserService {

    Optional<User> fetchUserByEmail(String email);
    List<ProgramDTO> fetchUserPrograms(String userId);
}
