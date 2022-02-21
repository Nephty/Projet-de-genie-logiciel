package com.example.demo.dao;

import com.example.demo.model.User;

import java.util.ArrayList;

public class FakeUserDao implements IUserDao {
    private final ArrayList<User> DB = new ArrayList<>();

    @Override
    public User fetchUser(String id) {
         User user = null;
         for(User u : DB) {
             if(u.getId().equals(id)) {
                 user = u;
             }
         }
         return user;
    }

    @Override
    public void insertUser(User user) {
        DB.add(user);
    }
}
