package com.example.demo.controller;

import com.example.demo.exception.throwables.ConflictException;
import com.example.demo.exception.throwables.ResourceNotFound;
import com.example.demo.model.Notification;
import com.example.demo.request.NotificationReq;
import com.example.demo.security.Role;
import com.example.demo.security.TokenHandler;
import com.example.demo.service.NotificationService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class NotificationControllerTest {

    @MockBean
    private NotificationService notificationService;

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
    void sendClientNotifications() throws Exception {
        // Given
        ArrayList<NotificationReq> res = new ArrayList<>();
        NotificationReq notification1 = new NotificationReq();
        notification1.setNotificationId(1);
        res.add(notification1);
        NotificationReq notification2 = new NotificationReq();
        notification2.setNotificationId(2);
        res.add(notification2);

        when(notificationService.getNotifications(any())).thenReturn(res);

        // Then
        mockMvc.perform(get("/api/notification")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].notificationId").value("1"))
                .andExpect(jsonPath("$[1].notificationId").value("2"));
    }

    @Test
    void sendClientNotificationShouldThrow404WhenClientNotFound() throws Exception {
        // Given

        when(notificationService.getNotifications(any())).thenThrow(new ResourceNotFound(""));

        // Then
        mockMvc.perform(get("/api/notification")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void addNotification() throws Exception {
        // Given
        NotificationReq notificationReq = new NotificationReq();
        notificationReq.setNotificationType(1);
        notificationReq.setComments("test");
        notificationReq.setRecipientId("userId");

        when(notificationService.addNotification(any(), eq(notificationReq)))
                .thenReturn(new Notification(notificationReq));

        // Then
        mockMvc.perform(post("/api/notification")
                        .header("Authorization", "Bearer " + token)
                        .content(asJsonString(notificationReq))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void addNotificationShouldThrow400WhenMissingParam() throws Exception {
        // Given
        NotificationReq notificationReq = new NotificationReq();
        notificationReq.setNotificationType(1);
        notificationReq.setComments(null);
        notificationReq.setRecipientId("userId");

        // Then
        mockMvc.perform(post("/api/notification")
                        .header("Authorization", "Bearer " + token)
                        .content(asJsonString(notificationReq))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addNotificationShouldThrow409WhenConflict() throws Exception {
        // Given
        NotificationReq notificationReq = new NotificationReq();
        notificationReq.setNotificationType(1);
        notificationReq.setComments("test");
        notificationReq.setRecipientId("userId");

        when(notificationService.addNotification(any(), eq(notificationReq)))
                .thenThrow(new ConflictException(""));

        // Then
        mockMvc.perform(post("/api/notification")
                        .header("Authorization", "Bearer " + token)
                        .content(asJsonString(notificationReq))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    void deleteNotification() throws Exception {
        // Given
        String id = "1";

        // Then
        mockMvc.perform(delete("/api/notification/" + id)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(id));
    }

    @Test
    void changeNotification() throws Exception {
        // Given
        NotificationReq notificationReq = new NotificationReq();
        notificationReq.setNotificationId(1);
        notificationReq.setIsFlagged(true);

        when(notificationService.changeNotification(notificationReq))
                .thenReturn(new Notification(notificationReq));

        // Then
        mockMvc.perform(put("/api/notification")
                        .header("Authorization", "Bearer " + token)
                        .content(asJsonString(notificationReq))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void changeNotificationShouldThrow400WhenMissingParam() throws Exception {
        // Given
        NotificationReq notificationReq = new NotificationReq();
        notificationReq.setNotificationId(1);
        notificationReq.setIsFlagged(null);

        // Then
        mockMvc.perform(put("/api/notification")
                        .header("Authorization", "Bearer " + token)
                        .content(asJsonString(notificationReq))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void changeNotificationShouldThrow404WhenNotificationNotFound() throws Exception {
        // Given
        NotificationReq notificationReq = new NotificationReq();
        notificationReq.setNotificationId(1);
        notificationReq.setIsFlagged(true);

        when(notificationService.changeNotification(notificationReq))
                .thenThrow(new ResourceNotFound(""));

        // Then
        mockMvc.perform(put("/api/notification")
                        .header("Authorization", "Bearer " + token)
                        .content(asJsonString(notificationReq))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}