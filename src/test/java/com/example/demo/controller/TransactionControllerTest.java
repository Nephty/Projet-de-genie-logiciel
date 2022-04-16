package com.example.demo.controller;

import com.example.demo.exception.throwables.AuthorizationException;
import com.example.demo.exception.throwables.ConflictException;
import com.example.demo.exception.throwables.ResourceNotFound;
import com.example.demo.model.TransactionLog;
import com.example.demo.request.TransactionReq;
import com.example.demo.security.Role;
import com.example.demo.security.TokenHandler;
import com.example.demo.service.TransactionLogService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class TransactionControllerTest {

    @MockBean
    private TransactionLogService transactionLogService;

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
    void makeTransfer() throws Exception {
        // Given
        TransactionReq transactionReq = new TransactionReq();
        transactionReq.setTransactionTypeId(1);
        transactionReq.setSenderIban("sender");
        transactionReq.setRecipientIban("recipient");
        transactionReq.setCurrencyId(1);
        transactionReq.setTransactionAmount(40.0);
        transactionReq.setComments("comment");
        transactionReq.setProcessed(null);

        TransactionLog transactionLog1 = new TransactionLog(transactionReq);
        transactionLog1.setIsSender(false);
        transactionLog1.setTransactionId(1);

        TransactionLog transactionLog2 = new TransactionLog(transactionReq);
        transactionLog2.setTransactionId(1);
        transactionLog2.setIsSender(true);

        ArrayList<TransactionLog> res = new ArrayList<>();
        res.add(transactionLog1);
        res.add(transactionLog2);

        when(transactionLogService.addTransaction(any(),eq(transactionReq)))
                .thenReturn(res);

        // Then
        mockMvc.perform(post("/api/transaction")
                        .header("Authorization", "Bearer " + token)
                        .content(asJsonString(transactionReq))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void makeTransferShouldThrow400WhenMissingParam() throws Exception {
        // Given
        TransactionReq transactionReq = new TransactionReq();
        transactionReq.setTransactionTypeId(1);
        transactionReq.setSenderIban("sender");
        transactionReq.setRecipientIban("recipient");
        transactionReq.setCurrencyId(null);
        transactionReq.setTransactionAmount(40.0);
        transactionReq.setComments("comment");
        transactionReq.setProcessed(null);

        // Then
        mockMvc.perform(post("/api/transaction")
                        .header("Authorization", "Bearer " + token)
                        .content(asJsonString(transactionReq))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void makeTransferShouldThrow403WhenBadAuthorisation() throws Exception {
        // Given
        TransactionReq transactionReq = new TransactionReq();
        transactionReq.setTransactionTypeId(1);
        transactionReq.setSenderIban("sender");
        transactionReq.setRecipientIban("recipient");
        transactionReq.setCurrencyId(1);
        transactionReq.setTransactionAmount(40.0);
        transactionReq.setComments("comment");
        transactionReq.setProcessed(null);

        when(transactionLogService.addTransaction(any(),eq(transactionReq)))
                .thenThrow(new AuthorizationException("Authorisation !"));

        // Then
        mockMvc.perform(post("/api/transaction")
                        .header("Authorization", "Bearer " + token)
                        .content(asJsonString(transactionReq))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$").value("Authorisation !"));
    }

    @Test
    void makeTransferShouldThrow409WhenConflict() throws Exception {
        // Given
        TransactionReq transactionReq = new TransactionReq();
        transactionReq.setTransactionTypeId(1);
        transactionReq.setSenderIban("sender");
        transactionReq.setRecipientIban("recipient");
        transactionReq.setCurrencyId(1);
        transactionReq.setTransactionAmount(40.0);
        transactionReq.setComments("comment");
        transactionReq.setProcessed(null);

        when(transactionLogService.addTransaction(any(),eq(transactionReq)))
                .thenThrow(new ConflictException("Conflict !"));

        // Then
        mockMvc.perform(post("/api/transaction")
                        .header("Authorization", "Bearer " + token)
                        .content(asJsonString(transactionReq))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$").value("Conflict !"));
    }



    @Test
    void sendTransfer() throws Exception{
        // Given
        String iban = "iban";
        int currencyId = 1;

        ArrayList<TransactionReq> res = new ArrayList<>();
        TransactionReq transactionReq = new TransactionReq();
        transactionReq.setTransactionTypeId(1);
        transactionReq.setSenderIban("sender");
        transactionReq.setRecipientIban("recipient");
        transactionReq.setCurrencyId(1);
        transactionReq.setTransactionAmount(40.0);
        transactionReq.setComments("comment");
        transactionReq.setProcessed(null);
        res.add(transactionReq);

        when(transactionLogService.getAllTransactionBySubAccount(iban,currencyId))
                .thenReturn(res);

        // Then
        mockMvc.perform(get("/api/transaction")
                        .header("Authorization", "Bearer " + token)
                        .param("iban",iban)
                        .param("currencyId",Integer.toString(currencyId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].senderIban").value("sender"))
                .andExpect(jsonPath("$[0].recipientIban").value("recipient"));
    }

    @Test
    void sendTransferShouldThrow400WhenMissingParam() throws Exception {
        // Given
        int currencyId = 1;

        // Then
        mockMvc.perform(get("/api/transaction")
                        .header("Authorization", "Bearer " + token)
                        .param("currencyId",Integer.toString(currencyId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void sendTransferShouldThrow404WhenSubAccountNotFound() throws Exception {
        // Given
        String iban = "iban";
        int currencyId = 1;

        when(transactionLogService.getAllTransactionBySubAccount(iban,currencyId))
                .thenThrow(new ResourceNotFound("Not found !"));

        // Then
        mockMvc.perform(get("/api/transaction")
                        .header("Authorization", "Bearer " + token)
                        .param("iban", iban)
                        .param("currencyId",Integer.toString(currencyId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").value("Not found !"));
    }
}