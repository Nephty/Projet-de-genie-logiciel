package com.example.demo.controller;

import com.example.demo.exception.throwables.ResourceNotFound;
import com.example.demo.model.User;
import com.example.demo.request.UserReq;
import com.example.demo.security.Role;
import com.example.demo.security.TokenHandler;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    private final User user1 = new User();
    private final User user2 = new User();

    private String token;

    static String asJsonString(final Object obj){
        try{
            return new ObjectMapper().writeValueAsString(obj);
        }catch (Exception e){
            throw new RuntimeException("error when wrap json");
        }
    }

    public String createToken(){
        TokenHandler tokenHandler = new TokenHandler();
        Map<String,String> result = tokenHandler.createTokens("testId","issuer", Role.USER);
        return result.get("access_token");
    }

    @BeforeEach
    void setUp() {
        user1.setUserId("testId");
        user1.setUsername("username1");
        user2.setUserId("test2Id");
        user2.setUsername("username2");

        token = createToken();

    }

        @Test
        void canSendUserByUserId() throws Exception{
            when(userService.getUserById(user1.getUserId()))
                    .thenReturn(new UserReq(user1));

            mockMvc.perform(get("/api/user/testId")
                            .header("Authorization","Bearer "+token)
                            .param("isUsername","false"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.userId").value("testId"))
                    .andExpect(jsonPath("$.username").value("username1"));
        }

        @Test
        void sendUserByUserIdShouldThrowWhenUserNotFound() throws Exception{
            when(userService.getUserById(user1.getUserId()))
                    .thenThrow(new ResourceNotFound(""));

            mockMvc.perform(get("/api/user/testId")
                            .header("Authorization","Bearer "+token)
                            .param("isUsername","false"))
                    .andExpect(status().isNotFound());
        }

        @Test
        void canSendUserByUsername() throws Exception{
            when(userService.getUserByUsername(user1.getUsername()))
                    .thenReturn(new UserReq(user1));

            mockMvc.perform(get("/api/user/username1")
                            .header("Authorization","Bearer "+token)
                            .param("isUsername","true"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.userId").value("testId"))
                    .andExpect(jsonPath("$.username").value("username1"));
        }

        @Test
        void sendUserByUsernameShouldThrowWhenUserNotFound() throws Exception{
            when(userService.getUserByUsername(user1.getUsername()))
                    .thenThrow(new ResourceNotFound(""));

            mockMvc.perform(get("/api/user/username1")
                            .header("Authorization","Bearer "+token)
                            .param("isUsername","true"))
                    .andExpect(status().isNotFound());
        }

    @Test
    @Disabled
    void sendAllUser() throws Exception{
        ArrayList<UserReq> res = new ArrayList<>();
        res.add(new UserReq(user1));
        res.add(new UserReq(user2));
        when(userService.getAllUser()).thenReturn(res);

        mockMvc.perform(get("/api/user")
                        .header("Authorization","Bearer "+token))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    void addUser() {
    }

    @Test
    void changeUser() {
    }
}