package com.example.demo.model;

import com.example.demo.request.NotificationReq;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;

import static javax.persistence.GenerationType.SEQUENCE;



@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "notification")
public class Notification {

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

    @ManyToOne
    @JoinColumn(
            name="user_id",
            referencedColumnName = "nrn",
            nullable = false
    )
    private User userId;

    @ManyToOne
    @JoinColumn(
            name="swift",
            referencedColumnName = "swift",
            nullable = false
    )
    private Bank bankId;

    @Column(name = "to_bank", nullable = false)
    private Boolean toBank = false;

    public Notification(NotificationReq notificationReq) {
        comments = notificationReq.getComments();
        date = new Date(System.currentTimeMillis());
        status = notificationReq.getStatus();
    }

}
