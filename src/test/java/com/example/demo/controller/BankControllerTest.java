package com.example.demo.controller;

import com.example.demo.exception.throwables.ConflictException;
import com.example.demo.exception.throwables.ResourceNotFound;
import com.example.demo.exception.throwables.UserAlreadyExist;
import com.example.demo.model.Bank;
import com.example.demo.model.User;
import com.example.demo.request.BankReq;
import com.example.demo.security.Role;
import com.example.demo.security.TokenHandler;
import com.example.demo.service.BankService;
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

import java.util.ArrayList;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
    void addBank() throws Exception {
        // Given
        BankReq bankReq = new BankReq();
        bankReq.setSwift("swift");
        bankReq.setName("name");
        bankReq.setPassword("pass");
        bankReq.setAddress("address");
        bankReq.setCountry("country");
        bankReq.setDefaultCurrencyId(0);

        when(bankService.addBank(bankReq)).thenReturn(new Bank(bankReq));

        // Then
        mockMvc.perform(post("/api/bank")
                        .header("Authorization", "Bearer " + token)
                        .content(asJsonString(bankReq))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void addBankShouldThrow400WhenMissingParam() throws Exception {
        // Given
        BankReq bankReq = new BankReq();
        bankReq.setSwift("swift");
        bankReq.setName(null);
        bankReq.setPassword("pass");
        bankReq.setAddress("address");
        bankReq.setCountry("country");
        bankReq.setDefaultCurrencyId(0);


        // Then
        mockMvc.perform(post("/api/bank")
                        .header("Authorization", "Bearer " + token)
                        .content(asJsonString(bankReq))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addBankShouldThrow403WhenBankAlreadyExist() throws Exception {
        // Given
        BankReq bankReq = new BankReq();
        bankReq.setSwift("swift");
        bankReq.setName("name");
        bankReq.setPassword("pass");
        bankReq.setAddress("address");
        bankReq.setCountry("country");
        bankReq.setDefaultCurrencyId(0);

        when(bankService.addBank(bankReq)).thenThrow(new UserAlreadyExist(UserAlreadyExist.Reason.ID));

        // Then
        mockMvc.perform(post("/api/bank")
                        .header("Authorization", "Bearer " + token)
                        .content(asJsonString(bankReq))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value("ID"));
    }

    @Test
    void addBankShouldThrow409WhenCurrencyNotFound() throws Exception {
        // Given
        BankReq bankReq = new BankReq();
        bankReq.setSwift("swift");
        bankReq.setName("name");
        bankReq.setPassword("pass");
        bankReq.setAddress("address");
        bankReq.setCountry("country");
        bankReq.setDefaultCurrencyId(0);

        when(bankService.addBank(bankReq)).thenThrow(new ConflictException("Hello there"));

        // Then
        mockMvc.perform(post("/api/bank")
                        .header("Authorization", "Bearer " + token)
                        .content(asJsonString(bankReq))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value("Hello there"));
    }

    @Test
    void sendBank() throws Exception {
        // Given
        String swift = "swift";
        BankReq bankReq = new BankReq();
        bankReq.setSwift(swift);

        when(bankService.getBank(swift)).thenReturn(bankReq);

        // Then
        mockMvc.perform(get("/api/bank/" + swift)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.swift").value(swift));
    }

    @Test
    void sendBankShouldThrow404WhenBankNotFound() throws Exception {
        // Given
        String swift = "swift";

        when(bankService.getBank(swift)).thenThrow(new ResourceNotFound(""));

        // Then
        mockMvc.perform(get("/api/bank/" + swift)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void sendAllBanks() throws Exception {
        // Given
        ArrayList<Bank> res = new ArrayList<>();
        Bank bank1 = new Bank();
        bank1.setSwift("bank1");
        res.add(bank1);
        Bank bank2 = new Bank();
        bank2.setSwift("bank2");
        res.add(bank2);

        when(bankService.getAllBanks()).thenReturn(res);

        // Then
        mockMvc.perform(get("/api/bank")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].swift").value("bank1"))
                .andExpect(jsonPath("$[1].swift").value("bank2"));
    }

    @Test
    void changeBank() throws Exception {
        // Given
        BankReq bankReq = new BankReq();
        bankReq.setPassword("test");

        when(bankService.changeBank(any(), eq(bankReq))).thenReturn(new Bank(bankReq));

        // Then
        mockMvc.perform(put("/api/bank")
                        .header("Authorization", "Bearer " + token)
                        .content(asJsonString(bankReq))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void changeBankShouldThrow400WhenMissingParam() throws Exception {
        // Given
        BankReq bankReq = new BankReq();

        // Then
        mockMvc.perform(put("/api/bank")
                        .header("Authorization", "Bearer " + token)
                        .content(asJsonString(bankReq))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void changeBankShouldThrow404WhenBankNotFound() throws Exception {
        // Given
        BankReq bankReq = new BankReq();
        bankReq.setPassword("test");

        when(bankService.changeBank(any(), eq(bankReq))).thenThrow(new ResourceNotFound(""));

        // Then
        mockMvc.perform(put("/api/bank")
                        .header("Authorization", "Bearer " + token)
                        .content(asJsonString(bankReq))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void changeBankShouldThrow409WhenBadFK() throws Exception {
        // Given
        BankReq bankReq = new BankReq();
        bankReq.setPassword("test");
        bankReq.setDefaultCurrencyId(15);

        when(bankService.changeBank(any(), eq(bankReq))).thenThrow(new ConflictException(""));

        // Then
        mockMvc.perform(put("/api/bank")
                        .header("Authorization", "Bearer " + token)
                        .content(asJsonString(bankReq))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    void getAllBankCustomers() throws Exception {
        // Given
        User user1 = new User();
        user1.setUserId("user1");
        User user2 = new User();
        user2.setUserId("user2");

        ArrayList<User> res = new ArrayList<>();
        res.add(user1);
        res.add(user2);

        when(bankService.getAllCustomersOfABank(any())).thenReturn(res);

        // Then
        mockMvc.perform(get("/api/bank/customer")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value("user1"))
                .andExpect(jsonPath("$[1].userId").value("user2"));
    }
}