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
@Table(name="user_notification")
public class UserNotification {

    @Column(name="notification_id")
    @Id
    private String notificationId;
    // TODO foreign key

    @Column(name="receiver_id")
    private String receiverId;
    // TODO foreign key

    @Column(name="sender_id")
    private String senderId;
    // TODO foreign key

}
