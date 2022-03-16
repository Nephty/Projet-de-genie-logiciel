package com.example.demo.service;

import com.example.demo.exception.throwables.MissingParamException;
import com.example.demo.exception.throwables.ResourceNotFound;
import com.example.demo.exception.throwables.UserAlreadyExist;
import com.example.demo.model.Bank;
import com.example.demo.model.User;
import com.example.demo.repository.BankRepo;
import com.example.demo.repository.UserRepo;
import com.example.demo.security.Role;
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
    private final BankRepo bankRepo;
    

    public User getUserById(String id) {
        return uRepo.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("No user with id: " + id)
                );
    }

    public User getUserByUsername(String username) {
        return uRepo.findByUsername(username)
                .orElseThrow(()-> new ResourceNotFound(username));
    }


    public List<User> getAllUser() {
        log.info("Fetching all user");
        return uRepo.findAll();
    }

    public void addUser(User user) {
        System.out.println(user.getPassword());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (uRepo.existsById(user.getUserID())){
            log.warn("User "+user.getUserID()+" already exists");
            throw new UserAlreadyExist(UserAlreadyExist.Reason.ID);
        }
        if (uRepo.existsByUsername(user.getUsername())){
            log.warn("Username "+user.getUsername()+" already exists");
            throw new UserAlreadyExist(UserAlreadyExist.Reason.USERNAME);
        }
        if (uRepo.existsByEmail(user.getEmail())){
            log.warn("Email "+user.getEmail()+" already exists");
            throw new UserAlreadyExist(UserAlreadyExist.Reason.EMAIL);
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
        String[] usernameRole = username.split("/");
        Collection<SimpleGrantedAuthority> authorities= new ArrayList<>();
        log.info("Attempt authentication by {} with role {}", usernameRole[0], usernameRole[1]);
        switch (usernameRole[1]) {
            case "ROLE_USER":
                User user = getUserCredentials(usernameRole[0]);
                authorities.add(new SimpleGrantedAuthority(Role.USER.getRole()));
                return new org.springframework.security.core.userdetails.User(
                        user.getUsername(), user.getPassword(), authorities
                );
            case "ROLE_BANK":
                Bank bank = getBankCredentials(usernameRole[0]);
                authorities.add(new SimpleGrantedAuthority(Role.BANK.getRole()));
                log.info("[BANK]{}", bank.toString());
                return new org.springframework.security.core.userdetails.User(
                        bank.getLogin(), bank.getPassword(), authorities
                );
            default:
                throw new UsernameNotFoundException("Incorrect role: " + usernameRole[1]);
        }
    }

    private User getUserCredentials(String username) {
        return uRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }

    private Bank getBankCredentials(String login) {
        return bankRepo.findByLogin(login)
                .orElseThrow(()-> {
                    log.error("no bank with such login: {}", login);
                    return new UsernameNotFoundException(login);
                });
    }

}
