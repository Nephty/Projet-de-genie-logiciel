package com.example.demo.model;


import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@EqualsAndHashCode
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="user_notification")
public class UserNotification implements Serializable {
    @Id
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(
            name = "notification_id",
            referencedColumnName = "notification_id"
    )
    private Notification notificationId;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(
            name = "receiver_id",
            referencedColumnName = "nrn"
    )
    private User receiverId;


    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(
            name = "sender_id",
            referencedColumnName = "swift"
    )
    private Bank senderId;

}
