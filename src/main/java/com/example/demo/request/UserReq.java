package com.example.demo.request;

import com.example.demo.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@AllArgsConstructor
@Data
public class UserReq {

    private String userId;

    private String username;

    private String lastname;

    private String firstname;

    private String email;

    private String password;

    private String language;

    private Date birthdate;

    @JsonIgnore
    public boolean isPostValid() {
        return userId != null
                && username != null
                && lastname != null
                && firstname != null
                && email != null
                && password != null
                && language != null
                && birthdate != null;
    }

    @JsonIgnore
    public boolean isPutValid() {
        return password != null || language != null;
    }

    public UserReq(User user) {
        userId = user.getUserId();
        username = user.getUsername();
        lastname = user.getLastname();
        firstname = user.getFirstname();
        email = user.getEmail();
        password = user.getPassword();
        language = user.getLanguage();
        birthdate = user.getBirthdate();
    }
}
