package com.example.demo.service;

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

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service @Transactional @Slf4j
public class UserService implements UserDetailsService {

    private final UserRepo uRepo;
    private final PasswordEncoder passwordEncoder;
    

    public User getUserById(String id) {
        return uRepo.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("No user with id: " + id)
                );
    }

    public User getUserByUsername(String username) throws UsernameNotFoundException{
        Optional<User> user = uRepo.findByUsername(username);

        if(user.isEmpty()) {
            log.error("User with username {} was not found", username);
            throw new UsernameNotFoundException("");
        }
        return user.get();


    }
    //TODO Add pagination to the getAll
    public List<User> getAllUser() {
        log.info("Fetching all user");
        return uRepo.findAll();
    }

    public void addUser(User user) {
        System.out.println(user.getPassword());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (uRepo.existsById(user.getUserID())){
            throw new EntityExistsException("User "+user.getUserID()+" already exists");
        }
        if (uRepo.existsByUsername(user.getUsername())){
            throw new EntityExistsException("Username "+user.getUsername()+" already exists");
        }
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
        Collection<SimpleGrantedAuthority> authorities= new ArrayList<>();
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getPassword(), authorities
        );
    }
}
