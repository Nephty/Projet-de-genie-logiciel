package com.example.demo.model;

import com.example.demo.model.CompositePK.BanksCustomersPK;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@EqualsAndHashCode
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "banks_notification")
public class BankNotification implements Serializable {

    @EmbeddedId
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(
            name = "notification_id",
            referencedColumnName = "notification_id"
    )
    private Notification notificationId;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(
            name="receiver_id",
            referencedColumnName = "swift"
    )
    private Bank receiverId;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(
            name="sender_id",
            referencedColumnName = "nrn"
    )
    private User senderId;

}
