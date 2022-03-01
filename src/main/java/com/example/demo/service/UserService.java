package com.example.demo.service;

import com.example.demo.exception.throwables.ResourceNotFound;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
@Service @Transactional @Slf4j
public class UserService implements UserDetailsService {

    private final UserRepo uRepo;
    private final PasswordEncoder passwordEncoder;
    

    public User getUserById(String id) {
        log.info("Fetching user with id of {}", id);
        return uRepo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFound("No user with id: " + id)
                );
    }

    public User getUserByUsername(String username) throws UsernameNotFoundException{
        User user = uRepo.findByUsername(username);

        if(user == null) {
            log.error("User with username {} was not found", username);
            throw new UsernameNotFoundException("");
        }

        return user;
    }
    //TODO Add pagination to the getAll
    public List<User> getAllUser() {
        log.info("Fetching all user");
        return uRepo.findAll();
    }

    public void addUser(User user) {
        log.info("Registering: {}", user);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        uRepo.save(user);
    }

    public void changeUser(User user) {
        log.info("Changing user to {}", user.toString());
        uRepo.save(user);
    }

    public void deleteUser(String id) {
        log.info("Deleting user with id of {}", id);
        uRepo.deleteById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = getUserByUsername(username);
        log.info("User found {}", user);
        Collection<SimpleGrantedAuthority> authorities= new ArrayList<>();
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getPassword(), authorities
        );
    }
}
