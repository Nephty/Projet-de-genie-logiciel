package com.example.demo.controller;

import com.example.demo.security.Role;
import com.example.demo.security.TokenHandler;
import com.example.demo.service.BankService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class BankControllerTest {

    @MockBean
    private BankService bankService;

    @Autowired
    private MockMvc mockMvc;

    private String token;

    static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("error when wrap json");
        }
    }

    public String createToken() {
        TokenHandler tokenHandler = new TokenHandler();
        Map<String, String> result = tokenHandler.createTokens("testId", "issuer", Role.USER);
        return result.get("access_token");
    }

    @BeforeEach
    void setUp() {
        token = createToken();
    }

    @Test
    @Disabled
    void addBank() {
    }

    @Test
    @Disabled
    void sendBank() {
    }

    @Test
    @Disabled
    void sendAllBanks() {
    }

    @Test
    @Disabled
    void changeBank() {
    }
}