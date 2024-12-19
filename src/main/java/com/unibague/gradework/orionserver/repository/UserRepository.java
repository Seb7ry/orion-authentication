package com.unibague.gradework.orionserver.repository;

import com.unibague.gradework.orionserver.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing {@link User} entities.
 * Extends {@link MongoRepository} to provide CRUD operations and custom query methods.
 */
@Repository
public interface UserRepository extends MongoRepository<User, String> {

    /**
     * Finds a {@link User} by their email address.
     *
     * @param email the email address of the user to search for.
     * @return an {@link Optional} containing the found user, or empty if no user is found.
     */
    Optional<User> findByEmail(String email);
}