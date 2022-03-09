package com.example.demo.model;

import com.example.demo.model.CompositePK.BanksCustomersPK;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "banks_notification")
public class BankNotification implements Serializable {

    @Id
    @OneToOne
    @JoinColumn(
            name = "notification_id",
            referencedColumnName = "notification_id"
    )
    private Notification notificationId;

    @ManyToOne
    @JoinColumn(
            name="receiver_id",
            referencedColumnName = "swift"
    )
    private Bank receiverId;

    @ManyToOne
    @JoinColumn(
            name="sender_id",
            referencedColumnName = "nrn"
    )
    private User senderId;

    @Override
    public int hashCode(){
        return notificationId.hashCode();
    }

    @Override
    public boolean equals(Object obj){
        return notificationId.equals(obj);
    }
}
