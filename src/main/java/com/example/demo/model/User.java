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
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

@Data @Slf4j
@AllArgsConstructor
@NoArgsConstructor
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

    public User(String userID){
        this.userId = userID;
    }

    public User(String userID, String username, String lastname,
                String firstname, String email, String language,Date birthdate) {
        this.userId = userID;
        this.username = username;
        this.lastname = lastname;
        this.firstname = firstname;
        this.email = email;
        this.language = language;
    }

    public User(UserReq userReq) {
        userId = userReq.getUserId();
        username = userReq.getUsername();
        firstname = userReq.getFirstname();
        lastname = userReq.getLastname();
        email = userReq.getEmail();
        password = userReq.getPassword();
        language = userReq.getLanguage();
    }

    public void change(UserReq userReq) {
        if(userReq.getPassword() != null) {
            password = userReq.getPassword();
        }
        if(userReq.getLanguage() != null) {
            language = userReq.getLanguage();
        }
    }
    @JsonIgnore
    public String getFullName() {
        return lastname + " " + firstname;
    }

    @JsonIgnore
    public Optional<Boolean> isAbove18() {
        int age = getAge();
        if(age == -1) {
            return Optional.empty();
        }
        return Optional.of(getAge() >= 18);
    }

    @JsonIgnore
    public int getAge() {
        int birthYear;
        int birthMonth = 0;
        int birthDay = 0;
        try {
            birthYear = Integer.parseInt(userId.substring(0, 2));
            birthMonth = Integer.parseInt(userId.substring(3, 5));
            birthDay = Integer.parseInt(userId.substring(6, 8));
        } catch(NumberFormatException e) {
            birthYear = -1;
            log.warn("User[{}] nrn doesn't contain birth year at expected place", userId);
        }
        ZonedDateTime now = ZonedDateTime.now();

        if(birthYear > now.getYear() - 2000) {
            birthYear += 1900;
        }else if(birthYear < now.getYear() - 2000 && birthYear >= 0) {
            birthYear += 2000;
        } else {
            return -1;
        }


        ZonedDateTime birth = ZonedDateTime.of(
                birthYear,
                birthMonth,
                birthDay,
                0,
                0,
                0,
                0,
                ZoneId.systemDefault()
        );

        ZonedDateTime age = now.minus(Duration.ofMillis(birth.toInstant().toEpochMilli()));

        return age.getYear();
    }

}
