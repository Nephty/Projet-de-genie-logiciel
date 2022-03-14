package com.example.demo.model;


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

    @JsonIgnore
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
}
