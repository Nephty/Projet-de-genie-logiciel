package com.example.demo.repository;

import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * All the db request on the {@link User} table.
 */
@Repository
public interface UserRepo extends JpaRepository<User, String> {

    /**
     * Find the user with the requested username.
     * @param username The username of the user went to find.
     * @return The User with the requested username.
     */
    @Query("SELECT u " +
            "FROM User u " +
            "WHERE u.username = ?1")
    Optional<User> findUserByUsername(String username);

    /**
     * Check if the username is already used.
     * @param username The username that may already exist
     * @return True if the username is already used, false otherwise
     */
    @Query("SELECT " +
            "CASE WHEN COUNT(U) > 0 " +
            "THEN true " +
            "ELSE false " +
            "END " +
            "FROM User U " +
            "WHERE U.username = ?1")
    boolean existsByUsername(String username);

    /**
     * Check if the email is already used.
     * @param email The email that may already exist
     * @return True if the email is already used, false otherwise
     */
    @Query("SELECT " +
            "CASE WHEN COUNT(U) > 0 " +
            "THEN true " +
            "ELSE false " +
            "END " +
            "FROM User U " +
            "WHERE U.email = ?1")
    boolean existsByEmail(String email);
}

