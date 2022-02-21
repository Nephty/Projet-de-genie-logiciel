package com.example.demo.service;

import com.example.demo.exception.throwables.ResourceNotFound;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepo uRepo;

    public UserService(UserRepo uRepo) {
        this.uRepo = uRepo;
    }
    

    public User getUser(String id) {
        return uRepo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFound("No user with id: " + id)
                );
    }

    public List<User> getAllUser() {
        return uRepo.findAll();
    }

    public void addUser(User user) {
        uRepo.save(user);
    }

    public void changeUser(User user) {
        uRepo.save(user);
    }

    public void deleteUser(String id) {
        uRepo.deleteById(id);
    }
}
