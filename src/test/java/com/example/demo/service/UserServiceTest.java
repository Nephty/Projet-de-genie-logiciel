package com.example.demo.service;

import com.example.demo.exception.throwables.ResourceNotFound;
import com.example.demo.exception.throwables.UserAlreadyExist;
import com.example.demo.model.User;
import com.example.demo.other.Sender;
import com.example.demo.repository.BankRepo;
import com.example.demo.repository.UserRepo;
import com.example.demo.request.UserReq;
import com.example.demo.security.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

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
                "EN"
        ));
        when(userRepo.findById(id)).thenReturn(user);

        //When
        User userReturned = underTest.getUserById(id);

        //Then
        verify(userRepo).findById(id);
        assertThat(userReturned).isEqualTo(user.get());
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
                "language"
        ));
        when(userRepo.findByUsername(username)).thenReturn(user);

        //When
        User returnedUser = underTest.getUserByUsername(username);

        //then
        verify(userRepo).findByUsername(username);
        assertThat(returnedUser).isEqualTo(user.get());
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
                "EN"
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

        assertEquals(userReq.getUserId(),capturedUser.getUserID());
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
                "EN"
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
                "EN"
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
                "EN"
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
                "EN"
        );
        Sender sender = new Sender("userId",Role.USER);

        Optional<User> user = Optional.of(new User(userReq));
        user.get().setUserID(sender.getId());
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
        assertEquals(sender.getId(),capturedUser.getUserID());
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
                "EN"
        );
        Sender sender = new Sender("userId",Role.USER);

        //then
        assertThatThrownBy(() -> underTest.changeUser(userReq,sender))
                .isInstanceOf(ResourceNotFound.class)
                .hasMessageContaining(sender.getId());

        verify(userRepo,never()).save(any());//The user is never saved.

    }
    @Test
    void canDeleteUser() {
        //Given
        String id = "testId";
        //When
        underTest.deleteUser(id);
        //Then
        verify(userRepo).deleteById(id);
    }


    @Test
    @Disabled
    void canLoadUserByUsername() {
        //TODO test this method ??
    }
}