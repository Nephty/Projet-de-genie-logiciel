package com.example.demo.request;

import lombok.Data;

import java.sql.Date;

@Data
public class NotificationReq {

    private Integer notificationId;

    private Integer notificationType;

    private String comments;

    private Date date;

    private String status;
}
