package com.example.demo.request;

import com.example.demo.model.Notification;
import lombok.Data;

import java.sql.Date;

@Data
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

    public NotificationReq(Notification notification) {
        if(notification.getToBank()) {
            senderName = notification.getUserId().getFullName();
            recipientId = notification.getBankId().getSwift();
        } else {
            senderName = notification.getBankId().getName();
            recipientId = notification.getUserId().getUserID();
        }
        date = notification.getDate();
        notificationId = notification.getNotificationId();
        status = notification.getStatus();
        comments = notification.getComments();
        notificationType = notification.getNotificationType().getNotificationTypeId();
        notificationTypeName = notification.getNotificationType().getNotificationTypeName();
    }

}
