package com.example.demo.model;


import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="user_notification")
public class UserNotification implements Serializable {
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

    @Override
    public int hashCode(){
        return notificationId.hashCode();
    }

    @Override
    public boolean equals(Object obj){
        return notificationId.equals(obj);
    }
}
