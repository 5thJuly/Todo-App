package com.app.todoapp.repository;

import com.app.todoapp.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<Users, Integer> {
    Users findByUsername(String username);

    Users findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

}
