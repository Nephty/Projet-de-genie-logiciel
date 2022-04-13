package com.example.demo.controller;

import com.example.demo.exception.throwables.AuthorizationException;
import com.example.demo.exception.throwables.ResourceNotFound;
import com.example.demo.model.Account;
import com.example.demo.request.AccountReq;
import com.example.demo.security.Role;
import com.example.demo.security.TokenHandler;
import com.example.demo.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

    @MockBean
    private AccountService accountService;

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
    void sendAccount() throws Exception {
        // Given
        String iban = "iban";

        AccountReq accountReq = new AccountReq();
        accountReq.setIban(iban);

        when(accountService.getAccount(iban)).thenReturn(accountReq);

        // Then
        mockMvc.perform(get("/api/account/" + iban)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.iban").value(iban));
    }

    @Test
    void sendAccountShouldThrow404WhenAccountNotFound() throws Exception {
        // Given
        String iban = "iban";

        when(accountService.getAccount(iban)).thenThrow(new ResourceNotFound(""));

        // Then
        mockMvc.perform(get("/api/account/" + iban)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    void createAccount() throws Exception {
        // Given
        AccountReq accountReq = new AccountReq();
        accountReq.setIban("iban");
        accountReq.setSwift("swift");
        accountReq.setUserId("userId");
        accountReq.setAccountTypeId(0);

        when(accountService.addAccount(accountReq)).thenReturn(new Account(accountReq));

        // Then
        mockMvc.perform(post("/api/account")
                        .header("Authorization", "Bearer " + token)
                        .content(asJsonString(accountReq))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void createAccountShouldThrow400WhenMissingParam() throws Exception {
        // Given
        AccountReq accountReq = new AccountReq();
        accountReq.setIban("iban");
        accountReq.setSwift("swift");
        accountReq.setUserId("userId");

        // Then
        mockMvc.perform(post("/api/account")
                        .header("Authorization", "Bearer " + token)
                        .content(asJsonString(accountReq))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createAccountShouldThrow409WhenBadType() throws Exception {
        // Given
        AccountReq accountReq = new AccountReq();
        accountReq.setIban("iban");
        accountReq.setSwift("swift");
        accountReq.setUserId("userId");
        accountReq.setAccountTypeId(4);
        accountReq.setPayment(true);

        when(accountService.addAccount(accountReq)).thenThrow(new AuthorizationException(""));

        // Then
        mockMvc.perform(post("/api/account")
                        .header("Authorization", "Bearer " + token)
                        .content(asJsonString(accountReq))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void changeAccount() throws Exception {
        // Given
        AccountReq accountReq = new AccountReq();
        accountReq.setIban("iban");
        accountReq.setPayment(true);
        accountReq.setAccountTypeId(0);

        when(accountService.changeAccount(accountReq))
                .thenReturn(new Account(accountReq));

        // Then
        mockMvc.perform(put("/api/account")
                        .header("Authorization", "Bearer " + token)
                        .content(asJsonString(accountReq))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void changeAccountShouldThrow400WhenMissingParam() throws Exception {
        // Given
        AccountReq accountReq = new AccountReq();

        // Then
        mockMvc.perform(put("/api/account")
                        .header("Authorization", "Bearer " + token)
                        .content(asJsonString(accountReq))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void changeAccountShouldThrow404WhenAccountNotFound() throws Exception {
        // Given
        AccountReq accountReq = new AccountReq();
        accountReq.setIban("iban");
        accountReq.setPayment(true);

        when(accountService.changeAccount(accountReq)).thenThrow(new ResourceNotFound(""));

        // Then
        mockMvc.perform(put("/api/account")
                        .header("Authorization", "Bearer " + token)
                        .content(asJsonString(accountReq))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void changeAccountShouldThrow409WhenBadType() throws Exception {
        // Given
        AccountReq accountReq = new AccountReq();
        accountReq.setIban("iban");
        accountReq.setPayment(true);

        when(accountService.changeAccount(accountReq)).thenThrow(new AuthorizationException(""));

        // Then
        mockMvc.perform(put("/api/account")
                        .header("Authorization", "Bearer " + token)
                        .content(asJsonString(accountReq))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteAccount() throws Exception {
        // Given
        String iban = "iban";

        Account res = new Account();
        res.setIban(iban);

        when(accountService.deleteAccount(iban)).thenReturn(res);

        // Then
        mockMvc.perform(delete("/api/account/" + iban)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void deleteAccountShouldThrow404WhenAccountNotFound() throws Exception {
        // Given
        String iban = "iban";

        when(accountService.deleteAccount(iban)).thenThrow(new ResourceNotFound(""));

        // Then
        mockMvc.perform(delete("/api/account/" + iban)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}