package com.example.demo.model;


import com.example.demo.request.UserReq;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;
import java.time.LocalDateTime;

/**
 * The model for the table users.
 * Check the entity relationShip diagram in the documentation if you need more info about this table <br>
 * Setters, Getters, NoArgsConstructor, AllArgsConstructor and ToString method are implemented by {@link lombok}
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@Slf4j
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Column(name="nrn")
    @Id
    private String userId;

    @Column(
            nullable = false,
            unique = true
    )
    private String username;

    @Column(
            nullable = false
    )
    private String lastname;

    @Column(
            nullable = false
    )

    private String firstname;

    @Column(
            nullable = false,
            unique = true
    )
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(
            nullable = false
    )
    private String language;

    @Column(
            nullable = false
    )
    private Date birthdate;

    /**
     * Custom constructor for User
     * @param userID User's od
     * @param username User's username
     * @param lastname User's lastname
     * @param firstname User's firstName
     * @param email User's email
     * @param language User's preferred language
     * @param birthdate User's birthdate
     */
    public User(String userID, String username, String lastname,
                String firstname, String email, String language,Date birthdate) {
        this.userId = userID;
        this.username = username;
        this.lastname = lastname;
        this.firstname = firstname;
        this.email = email;
        this.language = language;
        this.birthdate = birthdate;
    }

    /**
     * Custom constructor for User with the custom req body.
     * @param userReq Custom request body for creating an account.
     */
    public User(UserReq userReq) {
        userId = userReq.getUserId();
        username = userReq.getUsername();
        firstname = userReq.getFirstname();
        lastname = userReq.getLastname();
        email = userReq.getEmail();
        password = userReq.getPassword();
        language = userReq.getLanguage();
        birthdate = userReq.getBirthdate();
    }

    /**
     * Changes the password and/or language of the user
     * @param userReq Custom request body for creating/modifying an account.
     */
    public void change(UserReq userReq) {
        if(userReq.getPassword() != null) {
            password = userReq.getPassword();
        }
        if(userReq.getLanguage() != null) {
            language = userReq.getLanguage();
        }
    }

    /**
     * Get the full name of a user (ex: Moreau Cyril)
     * @return A String containing the full name of the user -> lastname firstname
     */
    @JsonIgnore
    public String getFullName() {
        return lastname + " " + firstname;
    }

    /**
     * @return true if the user is above or equal to 18 yo
     * @see #getAge()
     */
    @JsonIgnore
    public boolean isAbove18() {
        return getAge() >= 18;
    }

    /**
     * @return The age of the user found with his birthdate.
     */
    @JsonIgnore
    public int getAge() {
        return LocalDateTime.now().getYear() - birthdate.toLocalDate().getYear();
    }
}
