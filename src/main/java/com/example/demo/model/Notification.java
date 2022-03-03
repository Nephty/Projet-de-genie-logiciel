package com.example.demo.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="notification")
public class Notification {
    @Column(name="notification_id") @Id
    private String notificationId;
    @Column(name="notification_type")
    private String notificationType;
    @Column
    private String comments;
    @Column
    private String date;
    @Column
    private String status;
}
