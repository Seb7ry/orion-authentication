package com.unibague.gradework.orionserver.repository;

import com.unibague.gradework.orionserver.model.Actor;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing {@link Actor} entities.
 * Extends {@link MongoRepository} to provide CRUD operations and custom query methods.
 */
@Repository
public interface ActorRepository extends MongoRepository<Actor, String> {

    /**
     * Finds an {@link Actor} by their email address.
     *
     * @param email the email of the actor to search for.
     * @return an {@link Optional} containing the found actor, or empty if no actor is found.
     */
    Optional<Actor> findByEmail(String email);
}
