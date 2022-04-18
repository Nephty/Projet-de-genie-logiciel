package com.example.demo.controller;

import com.example.demo.exception.throwables.ConflictException;
import com.example.demo.exception.throwables.ResourceNotFound;
import com.example.demo.model.AccountAccess;
import com.example.demo.model.User;
import com.example.demo.request.AccountAccessReq;
import com.example.demo.security.Role;
import com.example.demo.security.TokenHandler;
import com.example.demo.service.AccountAccessService;
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
class AccountAccessControllerTest {

    @MockBean
    private AccountAccessService accountAccessService;

    @Autowired
    private MockMvc mockMvc;

    private String token;
    private final User user1 = new User();

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
        user1.setUserId("testId");

        token = createToken();
    }

    @Test
    void sendAccountAccess() throws Exception {
        // Given
        String userId = "userId";

        ArrayList<AccountAccessReq> res = new ArrayList<>();
        AccountAccessReq access1 = new AccountAccessReq(
                "accountId",
                userId,
                true,
                false
        );
        res.add(access1);

        AccountAccessReq access2 = new AccountAccessReq(
                "account2Id",
                userId,
                true,
                false
        );
        res.add(access2);

        when(accountAccessService.getAccountAccessByUserId(userId))
                .thenReturn(res);

        // Then
        mockMvc.perform(get("/api/account-access/all")
                        .header("Authorization", "Bearer " + token)
                        .param("userId", userId)
                        .param("deleted", "false")
                        .param("hidden", "false"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$[0].userId").value(userId))
                .andExpect(jsonPath("$[1].userId").value(userId));
    }

    @Test
    void sendAccountAccessShouldThrow400WhenMissingParam() throws Exception {
        // Given
        String userId = "userId";

        // Then
        mockMvc.perform(get("/api/account-access/all")
                        .header("Authorization", "Bearer " + token)
                        .param("userId", userId)
                        .param("deleted", "false"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void sendAccountAccessShouldThrow404WhenUserNotFound() throws Exception {
        // Given
        String userId = "userId";

        when(accountAccessService.getAccountAccessByUserId(userId))
                .thenThrow(new ResourceNotFound(""));

        // Then
        mockMvc.perform(get("/api/account-access/all")
                        .header("Authorization", "Bearer " + token)
                        .param("userId", userId)
                        .param("deleted", "false")
                        .param("hidden","false"))
                .andExpect(status().isNotFound());
    }

    @Test
    void sendAccessToDeletedAccount() throws Exception {
        // Given
        String userId = "userId";

        ArrayList<AccountAccessReq> res = new ArrayList<>();
        AccountAccessReq access1 = new AccountAccessReq(
                "accountId",
                userId,
                true,
                false
        );
        res.add(access1);

        AccountAccessReq access2 = new AccountAccessReq(
                "account2Id",
                userId,
                true,
                false
        );
        res.add(access2);

        when(accountAccessService.getAccessToDeletedAccount(userId))
                .thenReturn(res);

        // Then
        mockMvc.perform(get("/api/account-access/all")
                        .header("Authorization", "Bearer " + token)
                        .param("userId", userId)
                        .param("deleted", "true")
                        .param("hidden", "false"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$[0].userId").value(userId))
                .andExpect(jsonPath("$[1].userId").value(userId));
    }

    @Test
    void sendAccessToDeletedAccountShouldThrow404WhenUserNotFound() throws Exception {
        // Given
        String userId = "userId";

        when(accountAccessService.getAccessToDeletedAccount(userId))
                .thenThrow(new ResourceNotFound(""));

        // Then
        mockMvc.perform(get("/api/account-access/all")
                        .header("Authorization", "Bearer " + token)
                        .param("userId", userId)
                        .param("deleted", "true")
                        .param("hidden","false"))
                .andExpect(status().isNotFound());
    }

    @Test
    void sendAccessToHiddenAccount() throws Exception {
        // Given
        String userId = "userId";

        ArrayList<AccountAccessReq> res = new ArrayList<>();
        AccountAccessReq access1 = new AccountAccessReq(
                "accountId",
                userId,
                true,
                true
        );
        res.add(access1);

        AccountAccessReq access2 = new AccountAccessReq(
                "account2Id",
                userId,
                true,
                true
        );
        res.add(access2);

        when(accountAccessService.getAccessToHiddenAccount(userId))
                .thenReturn(res);

        // Then
        mockMvc.perform(get("/api/account-access/all")
                        .header("Authorization", "Bearer " + token)
                        .param("userId", userId)
                        .param("deleted", "false")
                        .param("hidden", "true"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$[0].userId").value(userId))
                .andExpect(jsonPath("$[1].userId").value(userId));
    }

    @Test
    void sendAccessToHiddenAccountShouldThrow404WhenUserNotFound() throws Exception {
        // Given
        String userId = "userId";

        when(accountAccessService.getAccessToHiddenAccount(userId))
                .thenThrow(new ResourceNotFound(""));

        // Then
        mockMvc.perform(get("/api/account-access/all")
                        .header("Authorization", "Bearer " + token)
                        .param("userId", userId)
                        .param("deleted", "false")
                        .param("hidden","true"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllOwners() throws Exception {
        // Given
        String iban = "testIban";

        ArrayList<User> res = new ArrayList<>();
        res.add(user1);
        User user = new User();
        user.setUserId("test2Id");
        res.add(user);

        when(accountAccessService.findAllOwners(iban))
                .thenReturn(res);

        // Then
        mockMvc.perform(get("/api/account-access/all/co-owner")
                        .header("Authorization", "Bearer " + token)
                        .param("iban",iban))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$[0].userId").value("testId"))
                .andExpect(jsonPath("$[1].userId").value("test2Id"));
    }

    @Test
    void getAllOwnersShouldThrow404WhenAccountNotFound() throws Exception{
        // Given
        String iban = "testIban";

        when(accountAccessService.findAllOwners(iban))
                .thenThrow(new ResourceNotFound(""));

        // Then
        mockMvc.perform(get("/api/account-access/all/co-owner")
                        .header("Authorization", "Bearer " + token)
                        .param("iban",iban))
                .andExpect(status().isNotFound());
    }

    @Test
    void sendAccountAccessByUserIdAndAccountId() throws Exception {
        // Given
        String userId = "userId";
        String iban = "iban";

        AccountAccessReq res = new AccountAccessReq(
                iban,
                userId,
                true,
                false
        );

        when(accountAccessService.findAccountAccess(iban,userId))
                .thenReturn(res);

        // Then
        mockMvc.perform(get("/api/account-access/"+userId+"/"+iban)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.accountId").value(iban));
    }

    @Test
    void sendAccountAccessByIdShouldThrow404WhenAccessNotFound() throws Exception  {
        // Given
        String userId = "userId";
        String iban = "iban";

        when(accountAccessService.findAccountAccess(iban,userId))
                .thenThrow(new ResourceNotFound(""));

        // Then
        mockMvc.perform(get("/api/account-access/"+userId+"/"+iban)
                        .header("Authorization", "Bearer " + token)
                        .param("iban",iban))
                .andExpect(status().isNotFound());
    }

    @Test
    void addAccess() throws Exception {
        // Given
        AccountAccessReq res = new AccountAccessReq(
                "iban",
                "userId",
                true,
                false
        );

        when(accountAccessService.createAccountAccess(res))
                .thenReturn(new AccountAccess(res));
        when(accountAccessService.bankOwnsAccount(any(),eq(res.getAccountId())))
                .thenReturn(true);

        // Then
        mockMvc.perform(post("/api/account-access/")
                        .header("Authorization", "Bearer " + token)
                        .content(asJsonString(res))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void addAccessShouldThrow400WhenMissingParam() throws Exception {
        // Given
        AccountAccessReq res = new AccountAccessReq(
                "iban",
                "userId",
                null,
                false
        );

        when(accountAccessService.bankOwnsAccount(any(),eq(res.getAccountId())))
                .thenReturn(true);

        // Then
        mockMvc.perform(post("/api/account-access/")
                        .header("Authorization", "Bearer " + token)
                        .content(asJsonString(res))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

     @Test
     void addAccessShouldThrow409WhenConflict() throws Exception {
         // Given
         AccountAccessReq res = new AccountAccessReq(
                 "iban",
                 "userId",
                 true,
                 false
         );

         when(accountAccessService.bankOwnsAccount(any(),eq(res.getAccountId())))
                 .thenReturn(true);

         when(accountAccessService.createAccountAccess(res))
                 .thenThrow(new ConflictException(""));

         // Then
         mockMvc.perform(post("/api/account-access/")
                         .header("Authorization", "Bearer " + token)
                         .content(asJsonString(res))
                         .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                 .andExpect(status().isConflict());
     }

    @Test
    void changeAccess() throws Exception {
        // Given
        AccountAccessReq res = new AccountAccessReq(
                "iban",
                "userId",
                true,
                false
        );

        when(accountAccessService.bankOwnsAccount(any(),eq(res.getAccountId())))
                .thenReturn(true);

        when(accountAccessService.changeAccountAccess(res))
                .thenReturn(new AccountAccess(res));

        // Then
        mockMvc.perform(put("/api/account-access/")
                        .header("Authorization", "Bearer " + token)
                        .content(asJsonString(res))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void changeAccessShouldThrow400WhenMissingParam() throws Exception {
        // Given
        AccountAccessReq res = new AccountAccessReq(
                "iban",
                "userId",
                null,
                null
        );
        when(accountAccessService.bankOwnsAccount(any(),eq(res.getAccountId())))
                .thenReturn(true);

        when(accountAccessService.changeAccountAccess(res))
                .thenReturn(new AccountAccess(res));

        // Then
        mockMvc.perform(put("/api/account-access/")
                        .header("Authorization", "Bearer " + token)
                        .content(asJsonString(res))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void changeAccessShouldThrow404WhenAccessNotFound() throws Exception {
        // Given
        AccountAccessReq res = new AccountAccessReq(
                "iban",
                "userId",
                true,
                false
        );

        when(accountAccessService.bankOwnsAccount(any(),eq(res.getAccountId())))
                .thenReturn(true);

        when(accountAccessService.changeAccountAccess(res))
                .thenThrow(new ResourceNotFound(""));

        // Then
        mockMvc.perform(put("/api/account-access/")
                        .header("Authorization", "Bearer " + token)
                        .content(asJsonString(res))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteAccess() throws Exception {
        // Given
        String userId = "userId";
        String iban = "iban";

        when(accountAccessService.bankOwnsAccount(any(),eq(iban)))
                .thenReturn(true);

        // Then
        mockMvc.perform(delete("/api/account-access/")
                        .header("Authorization", "Bearer " + token)
                        .param("accountId",iban)
                        .param("userId",userId)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }
}