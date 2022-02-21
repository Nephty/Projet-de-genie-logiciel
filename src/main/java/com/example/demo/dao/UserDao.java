package com.example.demo.dao;

import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public class UserDao implements IUserDao {
    @Override
    public User fetchUser(String id) {
        return null;
    }

    @Override
    public void insertUser(User user) {

    }
}
