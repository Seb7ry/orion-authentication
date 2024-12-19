package com.unibague.gradework.orionserver.repository;

import com.unibague.gradework.orionserver.model.Student;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing {@link Student} entities.
 * Extends {@link MongoRepository} to provide CRUD operations and custom query methods.
 */
@Repository
public interface StudentRepository extends MongoRepository<Student, String> {

    /**
     * Finds a {@link Student} by their email address.
     *
     * @param email the email of the student to search for.
     * @return an {@link Optional} containing the found student, or empty if no student is found.
     */
    Optional<Student> findByEmail(String email);
}