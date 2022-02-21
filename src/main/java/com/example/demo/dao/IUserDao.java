package com.example.demo.dao;

import com.example.demo.model.User;

public interface IUserDao {

    User fetchUser(String id);
    void insertUser(User user);
}
