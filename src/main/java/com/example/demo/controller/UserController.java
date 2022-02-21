package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping(path = "user")
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

    @DeleteMapping(value = "{id}")
    public void deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
    }


}
