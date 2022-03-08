package com.example.demo.model;

import lombok.*;

import javax.persistence.*;
import java.sql.Date;
import java.text.DateFormat;

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

    @ManyToOne
    @JoinColumn(
            name="notification_type",
            referencedColumnName = "notification_type_id"
    )
    private NotificationType notificationType;

    @Column
    private String comments;
    @Column
    private Date date;
    @Column
    private String status;
}
