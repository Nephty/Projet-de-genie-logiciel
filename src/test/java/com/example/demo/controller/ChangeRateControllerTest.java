package com.example.demo.controller;

import com.example.demo.exception.throwables.ResourceNotFound;
import com.example.demo.model.ChangeRate;
import com.example.demo.request.ChangeRateReq;
import com.example.demo.security.Role;
import com.example.demo.security.TokenHandler;
import com.example.demo.service.ChangeRateService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Date;
import java.time.LocalDate;
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
public class ChangeRateControllerTest {

    @MockBean
    private ChangeRateService changeRateService;

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
    void sendChangeRate() throws Exception {
        // Given
        Date date = Date.valueOf(LocalDate.of(2022,4,20));
        int currencyFrom = 0; // EUR
        int currencyTo = 1; // USD

        ChangeRateReq res = new ChangeRateReq(
                date,
                currencyFrom,
                "EUR",
                currencyTo,
                "USD",
                1.02
        );
        when(changeRateService.getChangeRate(date,currencyFrom,currencyTo))
                .thenReturn(res);

        // Then
        mockMvc.perform(get("/api/change-rate")
                        .header("Authorization", "Bearer " + token)
                        .param("date",date.toString())
                        .param("currencyFrom",Integer.toString(currencyFrom))
                        .param("currencyTo",Integer.toString(currencyTo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currencyTypeFrom").value(currencyFrom))
                .andExpect(jsonPath("$.currencyTypeTo").value(currencyTo))
                .andExpect(jsonPath("$.date").value(date.toString()));
    }

    @Test
    void sendChangeRateShouldThrow404WhenNotFound() throws Exception {
        // Given
        Date date = Date.valueOf(LocalDate.of(2022,4,20));
        int currencyFrom = 0; // EUR
        int currencyTo = 1; // USD

        when(changeRateService.getChangeRate(date,currencyFrom,currencyTo))
                .thenThrow(new ResourceNotFound("Not found !"));

        // Then
        mockMvc.perform(get("/api/change-rate")
                        .header("Authorization", "Bearer " + token)
                        .param("date",date.toString())
                        .param("currencyFrom",Integer.toString(currencyFrom))
                        .param("currencyTo",Integer.toString(currencyTo)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").value("Not found !"));
    }

    @Test
    void sendChangeRateShouldThrow400WhenMissingParam() throws Exception {
        // Given
        Date date = Date.valueOf(LocalDate.of(2022,4,20));
        int currencyFrom = 0; // EUR
        int currencyTo = 1; // USD

        // Then
        mockMvc.perform(get("/api/change-rate")
                        .header("Authorization", "Bearer " + token)
                        .param("date",date.toString())
                        .param("currencyFrom",Integer.toString(currencyFrom)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void sendHistory() throws Exception {
        // Given
        Date date = Date.valueOf(LocalDate.of(2022,4,20));
        int currencyFrom = 0; // EUR
        int currencyTo = 1; // USD

        ChangeRateReq req = new ChangeRateReq(
                date,
                currencyFrom,
                "EUR",
                currencyTo,
                "USD",
                1.02
        );

        ArrayList<ChangeRateReq> res = new ArrayList<>();
        res.add(req);

        when(changeRateService.getChangeRateHistory(currencyFrom,currencyTo))
                .thenReturn(res);

        // Then
        mockMvc.perform(get("/api/change-rate/history")
                        .header("Authorization", "Bearer " + token)
                        .param("currencyFrom",Integer.toString(currencyFrom))
                        .param("currencyTo",Integer.toString(currencyTo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].currencyTypeFrom").value(currencyFrom))
                .andExpect(jsonPath("$[0].currencyTypeTo").value(currencyTo))
                .andExpect(jsonPath("$[0].date").value(date.toString()));
    }

    @Test
    void sendHistoryShouldThrow404WhenCurrencyNotFound() throws Exception {
        // Given
        Date date = Date.valueOf(LocalDate.of(2022,4,20));
        int currencyFrom = 0; // EUR
        int currencyTo = 1; // USD

        when(changeRateService.getChangeRateHistory(currencyFrom,currencyTo))
                .thenThrow(new ResourceNotFound("Not found !"));

        // Then
        mockMvc.perform(get("/api/change-rate/history")
                        .header("Authorization", "Bearer " + token)
                        .param("currencyFrom",Integer.toString(currencyFrom))
                        .param("currencyTo",Integer.toString(currencyTo)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").value("Not found !"));
    }


}
