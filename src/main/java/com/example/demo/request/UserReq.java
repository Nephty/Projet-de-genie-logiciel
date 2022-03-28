package com.example.demo.request;

import com.example.demo.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpMethod;

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

    public boolean isPostValid() {
        return userId != null
                && username != null
                && lastname != null
                && firstname != null
                && email != null
                && password != null
                && language != null;
    }

    public boolean isPutValid() {
        return password != null || language != null;
    }

    public UserReq(User user) {
        userId = user.getUserID();
        username = user.getUsername();
        lastname = user.getLastname();
        firstname = user.getFirstname();
        email = user.getEmail();
        password = user.getPassword();
        language = user.getLanguage();
    }
}
