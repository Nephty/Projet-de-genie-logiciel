package com.example.demo.Controller;

import com.example.demo.controller.UserController;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.web.JsonPath;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
/*
 * This class test almost all the request to the api.
 * We use a mock for the repository because we don't want to test on the real DB.
 */
public class UserControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;
    @Mock
    UserRepo userRepo;

    User user1 = new User("testNRN","test","Moreau","Cyril","test@gmail.com",
    "1234Test","EN");
    User use2 = new User("test2NRN","test2","Moreau","Cyril","test2@gmail.com",
            "1234Test","FR");

    // GET TEST
    @Test
    public void getUserById_test() throws Exception{
        Mockito.when(userRepo.findById("testNRN")).thenReturn(Optional.ofNullable(user1));

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/user/testNRN")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",hasSize(3)))
                .andExpect(jsonPath("$.username", Matchers.is("test")));
    }
}
