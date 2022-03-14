package com.example.demo.controller;


import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepo;
import com.example.demo.security.Role;
import com.example.demo.security.TokenHandler;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
@RequestMapping(path = "/api/user")
@RestController
public class UserController {
    private final UserService userService;

    @GetMapping(value = "{id}")
    public User sendUser(@PathVariable String id) {
        return userService.getUserById(id);
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

    @DeleteMapping(value = "{id}")
    public void deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
    }

    @GetMapping(value = "/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        TokenHandler jwtHandler = new TokenHandler();
        DecodedJWT decodedJWT = jwtHandler.extractToken(authorizationHeader);
        String username = decodedJWT.getSubject();
        User user = userService.getUserByUsername(username);
        Map<String, String> tokens = jwtHandler.createTokens(
                user.getUsername(),
                request.getRequestURL().toString(),
                Role.USER
        );
        tokens.put("refresh_token", authorizationHeader.substring("Bearer ".length()));
        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), tokens);
    }


}
