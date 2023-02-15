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

    /**
     * Check if the request body is ok for posting.
     *
     * <br>To post a Notification we need at least :
     * <ul>
     *     <li>NotificationType</li>
     *     <li>comments</li>
     *     <li>recipientID</li>
     * </ul>
     * @return true if the request body is valid for posting a notification
     */
    @JsonIgnore
    public boolean isPostValid() {
        return notificationType != null
                && comments != null
                && recipientId != null;
    }

    /**
     * Checks if the request body is ok for modifying.
     *
     * <br>To modify a Notification we need at least :
     * <ul>
     *     <li>notification id</li>
     *     <li>isFlagged</li>
     * </ul>
     * @return true if the request body is valid for modifying Notification.
     */
    @JsonIgnore
    public boolean isPutValid() {
        return notificationId != null
                && isFlagged != null;
    }

    /**
     * Creates a request body for Notification with a {@link Notification} object.
     * If the notification is from a User to a Bank, it sets the sender and the recipient correctly.
     * Same for a notification from a Bank to a User.
     * @param notification The Notification we want to get the request body.
     */
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
