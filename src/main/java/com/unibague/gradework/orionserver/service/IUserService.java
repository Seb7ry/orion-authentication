package com.unibague.gradework.orionserver.service;

import com.unibague.gradework.orionserver.model.UserLogDTO;

public interface IUserService {
    UserLogDTO getUserByEmail(String email);
}
