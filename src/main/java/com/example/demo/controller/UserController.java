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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    /**
     * @param id [path] id of the User
     * @return The user with the correspondent id
     * 200 - User found
     * 404 - NO user with such id
     */
    @GetMapping(value = "{id}")
    public ResponseEntity<User> sendUser(@PathVariable String id) {
         return new ResponseEntity<>(userService.getUserById(id), HttpStatus.OK);
    }

    /**
     * @return List with all the Users
     * 200 - All users
     */
    @GetMapping
    public ResponseEntity<List<User>> sendAllUser() {
        return new ResponseEntity<>(userService.getAllUser(), HttpStatus.OK);
    }

    /**
     * @param user [body] User to insert into the DB
     * @return User to String in the body
     * 201 - User added
     * 400 - Bad Format
     */
    @PostMapping
    public ResponseEntity<String> addUser(@RequestBody User user) {
        userService.addUser(user);
        return new ResponseEntity<>(user.toString(),HttpStatus.CREATED);
    }

    /**
     * @param user [body] User to change into the DB
     * @return User to String in the body
     * 201 - User modified
     * 400 - Bad Format
     */
    @PutMapping
    public ResponseEntity<String> changeUser(@RequestBody User user) {
        userService.changeUser(user);
        return new ResponseEntity<>(user.toString(),HttpStatus.CREATED);
    }

    /**
     * @param id [path] id of the user to delete
     * @return id received
     * 200 - Success
     */
    @DeleteMapping(value = "{id}")
    public ResponseEntity<String> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(id,HttpStatus.OK);
    }

    /**
     * @param request Http request
     * @param response Http response
     * Send a new access and request token in the response body
     */
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
