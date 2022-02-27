package com.example.demo.controller;


import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.demo.model.User;
import com.example.demo.security.Role;
import com.example.demo.security.TokenHandler;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequestMapping(path = "/api/user")
@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "{id}")
    public User sendUser(@PathVariable String id) {
        return userService.getUser(id);
    }

    @GetMapping
    public List<User> sendAllUser() {
        return userService.getAllUser();
    }

    @PostMapping
    public void addUser(@RequestBody User user) {
        System.out.println(user);
        userService.addUser(user);
    }

    @PutMapping
    public void changeUser(@RequestBody User user) {
        userService.changeUser(user);
    }

    @DeleteMapping
    public void deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
    }

    @GetMapping(value = "/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        TokenHandler jwtHandler = new TokenHandler();
        DecodedJWT decodedJWT = jwtHandler.extractToken(authorizationHeader);
        String email = decodedJWT.getSubject();
        User user = userService.getUserByEmail(email);
        Map<String, String> tokens = jwtHandler.createTokens(
                user.getEmail(),
                request.getRequestURL().toString(),
                Role.USER
        );
        tokens.put("refresh_token", authorizationHeader.substring("Bearer ".length()));
        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), tokens);
    }


}
