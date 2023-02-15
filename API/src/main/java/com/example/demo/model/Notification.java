package com.example.demo.model;

import com.example.demo.request.NotificationReq;
import lombok.*;

import javax.persistence.*;
import java.sql.Date;

import static javax.persistence.GenerationType.SEQUENCE;


/**
 * The model for the table accounts.
 * A notification is a message between a Bank and a User, to know the sender and the receiver of the message,
 * there is a parameter {@link #toBank}. <br>
 * Check the entity relationShip diagram in the documentation if you need more info about this table <br>
 * Setters, Getters, NoArgsConstructor, AllArgsConstructor and ToString method are implemented by {@link lombok}
 * @see NotificationType
 */
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

    @Column(
            nullable = false,
            name = "is_flagged"
    )
    private Boolean isFlagged;

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

    @Column(
            name = "to_bank",
            nullable = false
    )
    private Boolean toBank = false;

    /**
     * Custom constructor for the Notification with the custom request body. <br>
     * Set the date to the current time of the application, the comments and if it's flagged or not.
     * @param notificationReq Custom request body for creating an account.
     */
    public Notification(NotificationReq notificationReq) {
        comments = notificationReq.getComments();
        date = new Date(System.currentTimeMillis());
        isFlagged = notificationReq.getIsFlagged() != null && notificationReq.getIsFlagged();
    }

    /**
     * Modify the Notification (only used for {@link #isFlagged}
     * @param notificationReq Custom request body.
     */
    public void change(NotificationReq notificationReq) {
        isFlagged = notificationReq.getIsFlagged();
    }

}
