package com.example.demo.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data @AllArgsConstructor
public class NotificationReq {

    private String notificationId;

    private Integer notificationType;

    private String comments;

    private Date date;

    private String status;
}
