package com.example.demo.repository;

import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepo extends JpaRepository<User, String> {

    Optional<User> findUserByUsername(String username);

    @Query("SELECT " +
            "CASE WHEN COUNT(U) > 0 " +
            "THEN true " +
            "ELSE false " +
            "END " +
            "FROM User U " +
            "WHERE U.username = ?1")
    boolean existsByUsername(String username);

    @Query("SELECT " +
            "CASE WHEN COUNT(U) > 0 " +
            "THEN true " +
            "ELSE false " +
            "END " +
            "FROM User U " +
            "WHERE U.email = ?1")
    boolean existsByEmail(String email);
}

