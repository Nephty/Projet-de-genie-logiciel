package com.example.demo.model;


import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="user_notification")
public class UserNotification {

    @Id
    @OneToOne
    @JoinColumn(
            name = "notification_id",
            referencedColumnName = "notification_id"
    )
    private Notification notificationId;

    @ManyToOne
    @JoinColumn(
            name = "receiver_id",
            referencedColumnName = "nrn"
    )
    private User receiverId;


    @ManyToOne
    @JoinColumn(
            name = "sender_id",
            referencedColumnName = "swift"
    )
    private Bank senderId;

}
