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


@Getter
@Setter
@ToString
@Slf4j
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

    @Column(
            nullable = false
    )
    private Date birthdate;

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
        this.birthdate = birthdate;
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
    public Boolean isAbove18() {
        return getAge() >= 18;
    }

    @JsonIgnore
    public int getAge() {
        return LocalDateTime.now().getYear() - birthdate.toLocalDate().getYear();
    }

}
