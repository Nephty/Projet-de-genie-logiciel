package com.example.demo.controller;


import com.example.demo.exception.throwables.MissingParamException;
import com.example.demo.model.User;
import com.example.demo.security.Role;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RequiredArgsConstructor
@RequestMapping(path = "/api/user")
@RestController @Slf4j
public class UserController {

    private final UserService userService;
    private final HttpServletRequest httpRequest;

    /**
     * @param id [param]id of the user to retrieve
     * @param isUsername [param]declare if the id provided is the nrn or the username of the user
     * @return User with matching id
     * 200 - OK
     * 404 - Not found
     * Who ? the user itself and any bank that has this user
     */
    @GetMapping(value = "{id}")
    public ResponseEntity<User> sendUser(@PathVariable String id, @RequestParam Boolean isUsername) {
        if(isUsername == null) {
            throw new MissingParamException("isUsername param must be present");
        }
        if(isUsername) {
            return new ResponseEntity<>(userService.getUserByUsername(id), HttpStatus.OK);
        }
        return new ResponseEntity<>(userService.getUserById(id), HttpStatus.OK);
    }

    /**
     * @return An array with all users
     * Who ? no one besides a bank for all it's user
     */
    @GetMapping
    public ResponseEntity<List<User>> sendAllUser() {
        log.info(
                "[userID] {}, [role] {}",
                httpRequest.getAttribute("userId").toString(),
                ((Role) httpRequest.getAttribute("role")).getRole()
                );
        return new ResponseEntity<>(userService.getAllUser(), HttpStatus.OK);
    }

    /**
     * @param user [body] user to be added to the added to the DB
     * @return user to String
     * 201 - Created
     * 400 - Bad Request
     * 403 - User already exist
     * Who ? anyone
     * What ? /
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
     * Who ? user itself
     * What ? token is probably invalid
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
     * Who ? user itself
     * What ? delete its assets
     */
    @DeleteMapping(value = "{id}")
    public ResponseEntity<String> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(id,HttpStatus.OK);
    }
}
