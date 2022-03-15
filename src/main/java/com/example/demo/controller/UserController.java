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
     * @param id id of the user to retrieve
     * @return User with matching id
     * 200 - OK
     * 404 - Not found
     */
    @GetMapping(value = "{id}")
    public ResponseEntity<User> sendUser(@PathVariable String id) {
         return new ResponseEntity<>(userService.getUserById(id), HttpStatus.OK);
    }

    /**
     * @return An array with all users
     */
    @GetMapping
    public ResponseEntity<List<User>> sendAllUser() {
        return new ResponseEntity<>(userService.getAllUser(), HttpStatus.OK);
    }

    /**
     * @param user [body] user to be added to the added to the DB
     * @return user to String
     * 201 - Created
     * 400 - Bad Request
     */
    @PostMapping
    public ResponseEntity<String> addUser(@RequestBody User user) {
        userService.addUser(user);
        return new ResponseEntity<>(user.toString(),HttpStatus.CREATED);
    }

    /**
     * @param user [body] user to be added to the changed in the DB
     * @return user to String
     * 201 - Created
     * 400 - Bad Request
     */
    @PutMapping
    public ResponseEntity<String> changeUser(@RequestBody User user) {
        userService.changeUser(user);
        return new ResponseEntity<>(user.toString(),HttpStatus.CREATED);
    }

    /**
     * @param id id of the user to delete in the DB
     * @return id sent
     * 200 - OK
     */
    @DeleteMapping(value = "{id}")
    public ResponseEntity<String> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(id,HttpStatus.OK);
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
