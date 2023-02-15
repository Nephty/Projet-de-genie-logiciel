package com.example.demo.service;

import com.example.demo.exception.throwables.ResourceNotFound;
import com.example.demo.exception.throwables.UserAlreadyExist;
import com.example.demo.model.Bank;
import com.example.demo.model.CurrencyType;
import com.example.demo.model.User;
import com.example.demo.other.Sender;
import com.example.demo.repository.BankRepo;
import com.example.demo.repository.UserRepo;
import com.example.demo.request.UserReq;
import com.example.demo.security.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepo userRepo;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private BankRepo bankRepo;

    private UserService underTest;

    @BeforeEach
    void setUp() {
        underTest = new UserService(userRepo,passwordEncoder,bankRepo);
    }

    @Test
    void canGetUserById() {
        //Given
        String id = "test";
        Optional<User> user = Optional.of(new User(
                id,
                "tested",
                "lastName",
                "firstName",
                "email@gmail.com",
                "pass",
                "EN",
                Date.valueOf(LocalDate.of(2002,10,31))
        ));
        when(userRepo.findById(id)).thenReturn(user);

        //When
        UserReq userReturned = underTest.getUserById(id);

        //Then
        verify(userRepo).findById(id);
        assertThat(userReturned).isEqualTo(new UserReq(user.get()));
    }

    @Test
    void shouldThrowWhenUserNotFoundById(){
        //Given
        String id = "test";

        //then
        assertThatThrownBy(() -> underTest.getUserById(id))
                .isInstanceOf(ResourceNotFound.class)
                .hasMessageContaining("No user with id: "+id);
    }

    @Test
    void canGetUserByUsername() {
        //given
        String username = "testedUsername";
        Optional<User> user = Optional.of(new User(
                "testId",
                username,
                "lastName",
                "firstName",
                "email",
                "password",
                "language",
                Date.valueOf(LocalDate.of(2002,10,31))
        ));
        when(userRepo.findUserByUsername(username)).thenReturn(user);

        //When
        UserReq returnedUser = underTest.getUserByUsername(username);

        //then
        verify(userRepo).findUserByUsername(username);
        assertThat(returnedUser).isEqualTo(new UserReq(user.get()));
    }

    @Test
    void shouldThrowWhenUserNotFoundByUsername(){
        //Given
        String username = "testUsername";

        //then
        assertThatThrownBy(() -> underTest.getUserByUsername(username))
                .isInstanceOf(ResourceNotFound.class)
                .hasMessageContaining("No user with this username: "+username);

    }

    @Test
    void canGetAllUser() {
        //when
        underTest.getAllUser();
        //then
        verify(userRepo).findAll();
    }

    @Test
    void canAddUser() {
        //Given
        UserReq userReq = new UserReq(
                "userId",
                "username",
                "lastName",
                "firstName",
                "email",
                "pass",
                "EN",
                Date.valueOf(LocalDate.of(2002,10,31))
        );
        when(passwordEncoder.encode(userReq.getPassword()))
                .thenReturn("EncodedPassword");

        //When
        underTest.addUser(userReq);

        //Then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepo).save(userArgumentCaptor.capture());
        User capturedUser = userArgumentCaptor.getValue();

        verify(passwordEncoder).encode("pass");
        assertEquals("EncodedPassword",capturedUser.getPassword());

        assertEquals(userReq.getUserId(),capturedUser.getUserId());
        assertEquals(userReq.getUsername(),capturedUser.getUsername());
        assertEquals(userReq.getLastname(),capturedUser.getLastname());
        assertEquals(userReq.getFirstname(),capturedUser.getFirstname());
        assertEquals(userReq.getEmail(),capturedUser.getEmail());
        assertEquals(userReq.getLanguage(),capturedUser.getLanguage());
    }

    @Test
    void shouldThrowWhenUserIdAlreadyExists() {
        //Given
        UserReq userReq = new UserReq(
                "userId",
                "username",
                "lastName",
                "firstName",
                "email",
                "pass",
                "EN",
                Date.valueOf(LocalDate.of(2002,10,31))
        );
        when(userRepo.existsById(userReq.getUserId()))
                .thenReturn(true);

        assertThatThrownBy(() -> underTest.addUser(userReq))
                .isInstanceOf(UserAlreadyExist.class)
                .hasMessageContaining("ID");

        verify(userRepo,never()).save(any());//The user is never saved.
    }

    @Test
    void shouldThrowWhenUsernameAlreadyExists() {
        //Given
        UserReq userReq = new UserReq(
                "userId",
                "username",
                "lastName",
                "firstName",
                "email",
                "pass",
                "EN",
                Date.valueOf(LocalDate.of(2002,10,31))
        );
        when(userRepo.existsByUsername(userReq.getUsername()))
                .thenReturn(true);

        assertThatThrownBy(() -> underTest.addUser(userReq))
                .isInstanceOf(UserAlreadyExist.class)
                .hasMessageContaining("USERNAME");

        verify(userRepo,never()).save(any());//The user is never saved.
    }

    @Test
    void shouldThrowWhenEmailAlreadyExists() {
        //given
        UserReq userReq = new UserReq(
                "userId",
                "username",
                "lastName",
                "firstName",
                "email",
                "pass",
                "EN",
                Date.valueOf(LocalDate.of(2002,10,31))
        );
        when(userRepo.existsByEmail(userReq.getEmail())).thenReturn(true);

        //then
        assertThatThrownBy(() -> underTest.addUser(userReq))
                .isInstanceOf(UserAlreadyExist.class)
                .hasMessageContaining("EMAIL");

        verify(userRepo,never()).save(any());//The user is never saved.
    }

    @Test
    void canChangeUser() {
        //Given
        UserReq userReq = new UserReq(
                null,
                "username",
                "lastName",
                "firstName",
                "email",
                "pass",
                "EN",
                Date.valueOf(LocalDate.of(2002,10,31))
        );
        Sender sender = new Sender("userId",Role.USER);

        Optional<User> user = Optional.of(new User(userReq));
        user.get().setUserId(sender.getId());
        when(userRepo.findById(sender.getId())).thenReturn(user);

        when(passwordEncoder.encode(userReq.getPassword()))
                .thenReturn("EncodedPassword");

        //when
        underTest.changeUser(userReq, sender);

        //then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepo).save(userArgumentCaptor.capture());
        User capturedUser = userArgumentCaptor.getValue();

        // The password is encoded correctly
        verify(passwordEncoder).encode("pass");
        assertEquals("EncodedPassword",capturedUser.getPassword());

        //The user saved is the good one
        assertEquals(sender.getId(),capturedUser.getUserId());
        assertEquals(userReq.getUsername(),capturedUser.getUsername());
        assertEquals(userReq.getLastname(),capturedUser.getLastname());
        assertEquals(userReq.getFirstname(),capturedUser.getFirstname());
        assertEquals(userReq.getEmail(),capturedUser.getEmail());
        assertEquals(userReq.getLanguage(),capturedUser.getLanguage());
    }

    @Test
    void shouldThrowWhenSenderIdNotFound(){
        //Given
        UserReq userReq = new UserReq(
                null,
                "username",
                "lastName",
                "firstName",
                "email",
                "pass",
                "EN",
                Date.valueOf(LocalDate.of(2002,10,31))
        );
        Sender sender = new Sender("userId",Role.USER);

        //then
        assertThatThrownBy(() -> underTest.changeUser(userReq,sender))
                .isInstanceOf(ResourceNotFound.class)
                .hasMessageContaining(sender.getId());

        verify(userRepo,never()).save(any());//The user is never saved.

    }

    @Test
    void canLoadUserByUsernameForUser() {
        //Given
        String username = "username";
        String usernameAndRole = username + "/" + Role.USER.getRole();
        User user = new User(
                "testId",
                username,
                "lastName",
                "firstName",
                "email",
                "password",
                "language",
                Date.valueOf(LocalDate.of(2002,10,31))
        );
        when(userRepo.findUserByUsername("username")).thenReturn(Optional.of(user));
        //When
        UserDetails userDetails = underTest.loadUserByUsername(usernameAndRole);
        //Then
        assertEquals(userDetails.getUsername(), user.getUserId());
        assertEquals(userDetails.getPassword(), user.getPassword());
    }

    @Test
    void loadUserByUsernameShouldThrowWhenUsernameNotFound(){
        //Given
        String username = "username";
        String usernameAndRole = username + "/" + Role.USER.getRole();

        //Then
        assertThatThrownBy(() -> underTest.loadUserByUsername(usernameAndRole))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining(username);
    }

    @Test
    void canLoadUserByUsernameForBank() {
        //Given
        String swift = "username";
        String usernameAndRole = swift + "/" + Role.BANK.getRole();
        Bank bank = new Bank(
                swift,
                "name",
                "password",
                "address",
                "US",
                new CurrencyType(
                        0,
                        "EUR"
                )
        );
        when(bankRepo.findById(swift)).thenReturn(Optional.of(bank));
        //When
        UserDetails userDetails = underTest.loadUserByUsername(usernameAndRole);
        //Then
        assertEquals(userDetails.getUsername(), bank.getSwift());
        assertEquals(userDetails.getPassword(), bank.getPassword());
    }

    @Test
    void loadUserByUsernameShouldThrowWhenBankNotFound(){
        //Given
        String swift = "username";
        String usernameAndRole = swift + "/" + Role.BANK.getRole();

        //When
        assertThatThrownBy(() -> underTest.loadUserByUsername(usernameAndRole))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining(swift);
    }
}