package com.example.demo.model;

import com.example.demo.request.NotificationReq;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.text.DateFormat;

import static javax.persistence.GenerationType.SEQUENCE;

@Getter
@EqualsAndHashCode
@Embeddable
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="notification")
public class Notification implements Serializable {

    @Id
    @SequenceGenerator(
            name = "notification_sequence",
            sequenceName = "notification_sequence",
            allocationSize = 1 // How much will the sequence increase from
    )
    @GeneratedValue(
            strategy = SEQUENCE,
            generator = "notification_sequence"
    )
    @Column(
            name="notification_id",
            updatable = false
    )
    private Integer notificationId;

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

    public Notification(NotificationType notificationType, String comments, Date date, String status) {
        this.notificationType = notificationType;
        this.comments = comments;
        this.date = date;
        this.status = status;
    }

    public Notification(NotificationReq notificationReq) {
        notificationId = notificationReq.getNotificationId();
        comments = notificationReq.getComments();
        date = notificationReq.getDate();
        status = notificationReq.getStatus();
    }

}
