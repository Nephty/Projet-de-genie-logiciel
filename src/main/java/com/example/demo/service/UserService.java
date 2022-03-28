package com.example.demo.service;

import com.example.demo.exception.throwables.LittleBoyException;
import com.example.demo.exception.throwables.ResourceNotFound;
import com.example.demo.exception.throwables.UserAlreadyExist;
import com.example.demo.model.Bank;
import com.example.demo.model.User;
import com.example.demo.other.Sender;
import com.example.demo.repository.BankRepo;
import com.example.demo.repository.UserRepo;
import com.example.demo.request.UserReq;
import com.example.demo.security.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                        new ResourceNotFound("No user with id: " + id)
                );
    }

    public User getUserByUsername(String username) {
        return uRepo.findUserByUsername(username)
                .orElseThrow(()-> new ResourceNotFound("No user with this username: "+ username));
    }


    public List<User> getAllUser() {
        log.info("Fetching all user");
        return uRepo.findAll();
    }

    public User addUser(UserReq userReq) {
        //sender is null because this method is not authenticated
        User user = instantiateUser(null, userReq, HttpMethod.POST);
        log.info(user.toString());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return uRepo.save(user);
    }

    public User changeUser(UserReq userReq, Sender sender) {
        //alreadyExists(userReq).orElseThrow(()-> new ResourceNotFound(userReq.toString()));
        User user = instantiateUser(sender, userReq, HttpMethod.PUT);
        log.info("Changing user to {}", user);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return uRepo.save(user);
    }

    public void deleteUser(String id) {
        log.info("Deleting user with id of {}", id);
        uRepo.deleteById(id);
    }

    /**
     * Raise an error if there already is a user in the DB with any of the params given
     * @param userId id of the user
     * @param username username
     * @param email email of the user
     * @throws UserAlreadyExist if the user already exists
     */
    private void alreadyExistsCheck(String userId, String username, String email) throws UserAlreadyExist{
        if (uRepo.existsById(userId)){
            log.warn("User " + userId + " already exists");
            throw new UserAlreadyExist(UserAlreadyExist.Reason.ID);
        }
        if (uRepo.existsByUsername(username)){
            log.warn("Username " + username + " already exists");
            throw new UserAlreadyExist(UserAlreadyExist.Reason.USERNAME);
        }
        if (uRepo.existsByEmail(email)){
            log.warn("Email " + email + " already exists");
            throw new UserAlreadyExist(UserAlreadyExist.Reason.EMAIL);
        }
    }

    /**
     * @param sender id and role of the client extracted from the JWT
     * @param userReq request made by the client
     * @param method http method used in the request
     * @return A User entity based on the user request
     * @throws ResourceNotFound if the user does not already exist in the DB in the case of a PUT method
     * @throws LittleBoyException if the method provided is neither PUT nor POST
     */
    private User instantiateUser(Sender sender,
                                 UserReq userReq,
                                 HttpMethod method
    ) throws ResourceNotFound, LittleBoyException {
        switch (method) {
            case POST:
                alreadyExistsCheck(userReq.getUserId(), userReq.getUsername(), userReq.getEmail());
                return new User(userReq);
            case PUT:
                User user = uRepo.findById(sender.getId())
                        .orElseThrow(()-> new ResourceNotFound(sender.getId()));
                user.change(userReq);
                //alreadyExistsCheck(user.getUserID(), user.getUsername(), user.getEmail());
                return user;
            default:
                log.error("Invalid method {}", method);
                throw new LittleBoyException();
        }
    }

    //--------------------------------------------------------------------------------------------//
    //LOGIN PART

    /**
     * @param usernameAndRole String containing the username and the role separated by a fronts-lash
     * @return UserDetails used by spring to authenticate the user
     * @throws UsernameNotFoundException if the role doesn't match ROLE_USER or ROLE_BANK
     */
    @Override
    public UserDetails loadUserByUsername(String usernameAndRole) throws UsernameNotFoundException {
        String[] usernameRole = usernameAndRole.split("/");
        Collection<SimpleGrantedAuthority> authorities= new ArrayList<>();
        log.info("Attempt authentication by {} with role {}", usernameRole[0], usernameRole[1]);
        switch (usernameRole[1]) {
            case "ROLE_USER":
                User user = getUserCredentials(usernameRole[0]);
                authorities.add(new SimpleGrantedAuthority(Role.USER.getRole()));
                log.info("user loaded by username {}", user);
                return new org.springframework.security.core.userdetails.User(
                        user.getUserID(), user.getPassword(), authorities
                );
            case "ROLE_BANK":
                Bank bank = getBankCredentials(usernameRole[0]);
                authorities.add(new SimpleGrantedAuthority(Role.BANK.getRole()));

                return new org.springframework.security.core.userdetails.User(
                        bank.getSwift(), bank.getPassword(), authorities
                );
            default:
                throw new UsernameNotFoundException("Incorrect role: " + usernameRole[1]);
        }
    }

    /**
     * @param username username provided in the authentication request
     * @return User entity matching the username
     * @throws UsernameNotFoundException if the username provided doesn't match any user in the DB
     */
    private User getUserCredentials(String username) throws UsernameNotFoundException {
        return uRepo.findUserByUsername(username)
                .orElseThrow(() -> {
                    log.warn("no user with such username: {}", username);
                    return new UsernameNotFoundException(username);
                });
    }

    /**
     * Fetch the bank data in the DB
     * @param swift PK of the bank
     * @return Bank entity
     * @throws UsernameNotFoundException if the swift provided doesn't match any bank in the DB
     */
    private Bank getBankCredentials(String swift) throws UsernameNotFoundException {
        return bankRepo.findById(swift)
                .orElseThrow(()-> {
                    log.error("no bank with such id: {}", swift);
                    return new UsernameNotFoundException(swift);
                });
    }

}
