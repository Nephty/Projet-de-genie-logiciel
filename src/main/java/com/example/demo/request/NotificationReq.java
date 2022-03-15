package com.example.demo.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Date;

@Data @AllArgsConstructor
public class NotificationReq {

    private Integer notificationId;

    private Integer notificationType;

    private String comments;

    private Date date;

    private String status;
}
