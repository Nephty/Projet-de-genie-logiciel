package com.example.demo.repository;

import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepo extends JpaRepository<User, String> {
    @Query("SELECT new User(s.userID,s.email,s.firstname,s.lastname,s.username,s.language) " +
            "FROM User s WHERE s.userID = ?1")
    Optional<User> findByIdWithoutPassword(String userID);

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}

