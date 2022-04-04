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

    private String status;

    private String recipientId;

    //Response only

    private Integer notificationId;

    private Date date;

    private String senderName;

    private String notificationTypeName;

    @JsonIgnore
    public boolean isPostValid() {
        return notificationType != null
                && comments != null
                && status != null
                && recipientId != null;
    }

    public NotificationReq(Notification notification) {
        if(notification.getToBank()) {
            senderName = notification.getUserId().getFullName();
            recipientId = notification.getBankId().getSwift();
        } else {
            senderName = notification.getBankId().getName();
            recipientId = notification.getUserId().getUserId();
        }
        date = notification.getDate();
        notificationId = notification.getNotificationId();
        status = notification.getStatus();
        comments = notification.getComments();
        notificationType = notification.getNotificationType().getNotificationTypeId();
        notificationTypeName = notification.getNotificationType().getNotificationTypeName();
    }
}
