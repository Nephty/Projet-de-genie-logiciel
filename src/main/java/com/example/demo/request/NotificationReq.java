package com.example.demo.request;

import com.example.demo.model.Notification;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@NoArgsConstructor
@Data @AllArgsConstructor
public class NotificationReq {

    private Integer notificationType;

    private String comments;

    private Boolean isFlagged;

    private String recipientId;

    private Integer notificationId;

    //Response only

    private Date date;

    private String senderName;

    private String senderId;

    private String notificationTypeName;

    @JsonIgnore
    public boolean isPostValid() {
        return notificationType != null
                && comments != null
                && recipientId != null;
    }

    @JsonIgnore
    public boolean isPutValid() {
        return notificationId != null
                && isFlagged != null;
    }

    public NotificationReq(Notification notification) {
        if(notification.getToBank()) {
            senderId = notification.getUserId().getUserId();
            senderName = notification.getUserId().getFullName();
            recipientId = notification.getBankId().getSwift();
        } else {
            senderId = notification.getBankId().getSwift();
            senderName = notification.getBankId().getName();
            recipientId = notification.getUserId().getUserId();
        }
        date = notification.getDate();
        notificationId = notification.getNotificationId();
        isFlagged = notification.getIsFlagged();
        comments = notification.getComments();
        notificationType = notification.getNotificationType().getNotificationTypeId();
        notificationTypeName = notification.getNotificationType().getNotificationTypeName();
    }
}
