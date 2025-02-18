package com.unibague.gradework.orionserver.service;

import com.unibague.gradework.orionserver.model.ProgramDTO;
import com.unibague.gradework.orionserver.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Interface defining user-related services.
 * Provides methods for retrieving user details and associated programs.
 */
public interface IUserService {

    /**
     * Retrieves a user by their email address.
     *
     * @param email The email of the user to be retrieved.
     * @return An {@link Optional} containing the {@link User} if found, otherwise empty.
     */
    Optional<User> fetchUserByEmail(String email);

    /**
     * Retrieves the list of programs associated with a specific user.
     *
     * @param userId The unique identifier of the user.
     * @return A {@link List} of {@link ProgramDTO} representing the user's associated programs.
     */
    List<ProgramDTO> fetchUserPrograms(String userId);
}
