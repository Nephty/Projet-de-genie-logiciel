package com.example.demo.request;

import lombok.AllArgsConstructor;
import lombok.Data;

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

}
