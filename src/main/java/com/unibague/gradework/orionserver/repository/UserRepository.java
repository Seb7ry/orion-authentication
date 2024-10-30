package com.unibague.gradework.orionserver.repository;

import com.unibague.gradework.orionserver.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long>
{
    Optional<User> findByUserName(String firstName);
}
