package com.unibague.gradework.orionserver.repository;

import com.unibague.gradework.orionserver.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
}
