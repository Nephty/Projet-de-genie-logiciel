package com.example.demo.controller;

import com.example.demo.exception.throwables.ConflictException;
import com.example.demo.exception.throwables.ResourceNotFound;
import com.example.demo.model.SubAccount;
import com.example.demo.request.SubAccountReq;
import com.example.demo.security.Role;
import com.example.demo.security.TokenHandler;
import com.example.demo.service.SubAccountService;
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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class SubAccountControllerTest {

    @MockBean
    private SubAccountService subAccountService;

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
    void getSubAccount() throws Exception{
        // Given
        String iban = "iban";
        Integer currencyId = 1;

        SubAccountReq subAccountReq = new SubAccountReq();
        subAccountReq.setIban(iban);
        subAccountReq.setCurrencyType(1);

        when(subAccountService.getSubAccount(iban,currencyId)).thenReturn(subAccountReq);

        // Then
        mockMvc.perform(get("/api/account/sub-account")
                        .header("Authorization", "Bearer " + token)
                        .param("iban",iban)
                        .param("currencyId",currencyId.toString())
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.iban").value(iban))
                .andExpect(jsonPath("$.currencyType").value("1"));
    }

    @Test
    void getSubAccountShouldThrow404WhenSubAccountNotFound() throws Exception {
        // Given
        String iban = "iban";
        Integer currencyId = 1;

        when(subAccountService.getSubAccount(iban,currencyId)).thenThrow(new ResourceNotFound("Not found !"));

        // Then
        mockMvc.perform(get("/api/account/sub-account")
                        .header("Authorization", "Bearer " + token)
                        .param("iban",iban)
                        .param("currencyId",currencyId.toString())
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").value("Not found !"));
    }

    @Test
    void getAllSubAccount() throws Exception {
        // Given
        String iban = "iban";

        SubAccountReq subAccountReq = new SubAccountReq(
                iban,
                1,
                200.0,
                "EUR"
        );

        ArrayList<SubAccountReq> result = new ArrayList<>();
        result.add(subAccountReq);
        when(subAccountService.getAllSubAccount(iban)).thenReturn(result);

        // Then
        mockMvc.perform(get("/api/account/sub-account/all")
                        .header("Authorization", "Bearer " + token)
                        .param("iban",iban)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].iban").value(iban))
                .andExpect(jsonPath("$[0].currencyType").value("1"));
    }

    @Test
    void getAllSubAccountShouldThrow404WhenAccountNotFound() throws Exception {
        // Given
        String iban = "iban";

        when(subAccountService.getAllSubAccount(iban)).thenThrow(new ResourceNotFound("Not found !"));

        // Then
        mockMvc.perform(get("/api/account/sub-account/all")
                        .header("Authorization", "Bearer " + token)
                        .param("iban",iban)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").value("Not found !"));
    }

    @Test
    void addSubAccount() throws Exception {
        // Given
        SubAccountReq subAccountReq = new SubAccountReq();
        subAccountReq.setIban("iban");
        subAccountReq.setCurrencyType(1);
        subAccountReq.setCurrentBalance(200.0);

        when(subAccountService.addSubAccount(subAccountReq)).thenReturn(new SubAccount(subAccountReq));

        // Then
        mockMvc.perform(post("/api/account/sub-account")
                        .header("Authorization", "Bearer " + token)
                        .content(asJsonString(subAccountReq))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void addSubAccountShouldThrow400WhenMissingParam() throws Exception {
        // Given
        SubAccountReq subAccountReq = new SubAccountReq();
        subAccountReq.setIban("iban");
        subAccountReq.setCurrencyType(1);
        subAccountReq.setCurrentBalance(null);

        // Then
        mockMvc.perform(post("/api/account/sub-account")
                        .header("Authorization", "Bearer " + token)
                        .content(asJsonString(subAccountReq))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addSubAccountShouldThrow409WhenConflict() throws Exception {
        // Given
        SubAccountReq subAccountReq = new SubAccountReq();
        subAccountReq.setIban("iban");
        subAccountReq.setCurrencyType(1);
        subAccountReq.setCurrentBalance(200.0);

        when(subAccountService.addSubAccount(subAccountReq)).thenThrow(new ConflictException("Conflict !"));

        // Then
        mockMvc.perform(post("/api/account/sub-account")
                        .header("Authorization", "Bearer " + token)
                        .content(asJsonString(subAccountReq))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$").value("Conflict !"));
    }

    @Test
    void deleteSubAccount() throws Exception {
        // Given
        String iban = "iban";
        int currencyId = 1;

        // Then
        mockMvc.perform(delete("/api/account/sub-account")
                        .header("Authorization", "Bearer " + token)
                        .param("iban",iban)
                        .param("currencyId", Integer.toString(currencyId))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(iban+" : "+currencyId));
    }

    @Test
    void changeSubAccount() throws Exception{
        // Given
        SubAccountReq subAccountReq = new SubAccountReq();
        subAccountReq.setIban("iban");
        subAccountReq.setCurrencyType(1);
        subAccountReq.setCurrentBalance(200.0);

        SubAccount res = new SubAccount(subAccountReq);

        when(subAccountService.changeSubAccount(subAccountReq))
                .thenReturn(res);

        // Then
        mockMvc.perform(put("/api/account/sub-account")
                        .header("Authorization", "Bearer " + token)
                        .content(asJsonString(subAccountReq))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").value(res.toString()));
    }

    @Test
    void changeSubAccountShouldThrow400WhenMissingParam() throws Exception {
        // Given
        SubAccountReq subAccountReq = new SubAccountReq();
        subAccountReq.setIban(null);
        subAccountReq.setCurrencyType(1);
        subAccountReq.setCurrentBalance(200.0);

        // Then
        mockMvc.perform(put("/api/account/sub-account")
                        .header("Authorization", "Bearer " + token)
                        .content(asJsonString(subAccountReq))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void changeSubAccountShouldThrowWhenConflict() throws Exception {
        // Given
        SubAccountReq subAccountReq = new SubAccountReq();
        subAccountReq.setIban("iban");
        subAccountReq.setCurrencyType(1);
        subAccountReq.setCurrentBalance(200.0);


        when(subAccountService.changeSubAccount(subAccountReq))
                .thenThrow(new ConflictException("Conflict !"));

        // Then
        mockMvc.perform(put("/api/account/sub-account")
                        .header("Authorization", "Bearer " + token)
                        .content(asJsonString(subAccountReq))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$").value("Conflict !"));
    }
}