package com.example.demo.model;


import com.example.demo.request.UserReq;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;


import javax.persistence.*;

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
    private String userID;

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
        this.userID = userID;
    }

    public User(String userID, String username, String lastname,
                String firstname, String email, String language) {
        this.userID = userID;
        this.username = username;
        this.lastname = lastname;
        this.firstname = firstname;
        this.email = email;
        this.language = language;
    }

    public User(UserReq userReq) {
        userID = userReq.getUserId();
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

    public boolean isInvalid() {
        return userID.length() <= 5
                || username.length() < 3
                || lastname.length() == 0
                || firstname.length() == 0
                || email.length() <= 5
                || password.length() <= 5
                || language.length() < 2;
    }
}
