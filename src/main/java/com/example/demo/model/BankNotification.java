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
@Table(name = "banks_notification")
public class BankNotification {

    @Column(name="notification_id") @Id
    private String notificationId;
    @Column(name="receiver_id")
    private String receiverId;
    @Column(name="sender_id")
    private String senderId;

}
