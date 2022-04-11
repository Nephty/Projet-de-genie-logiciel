package com.example.demo.model;


import com.example.demo.request.UserReq;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@ToString
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
                String firstname, String email, String language) {
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
    //TODO age
    @JsonIgnore
    public boolean isAbove18() {
        return true;
    }

    @JsonIgnore
    public int getAge() {return 24;}

}
