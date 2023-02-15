package com.example.demo.request;

import com.example.demo.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;


@AllArgsConstructor
@NoArgsConstructor
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

    /**
     * Check if the request body is ok for posting.
     *
     * <br>To post a User we need at least :
     * <ul>
     *     <li>userId</li>
     *     <li>Username</li>
     *     <li>firstname</li>
     *     <li>email</li>
     *     <li>password</li>
     *     <li>language</li>
     *     <li>birthdate</li>
     * </ul>
     * @return true if the request body is valid for posting a User
     */
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

    /**
     * Checks if the request body is ok for modifying.
     *
     * <br>To modify a User we need at least :
     * <ul>
     *     <li>Password and/or language</li>
     * </ul>
     * @return true if the request body is valid for modifying a User.
     */
    @JsonIgnore
    public boolean isPutValid() {
        return password != null || language != null;
    }

    /**
     * Creates a request body for a User with a {@link User} object.
     * @param user The User we want to get the request body.
     */
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
