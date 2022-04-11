package com.example.demo.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The model for the table notification_type.
 * Check the entity relationShip diagram in the documentation if you need more info about this table <br>
 * Setters, Getters, NoArgsConstructor, AllArgsConstructor and ToString method are implemented by {@link lombok}
 * @see Notification
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="notification_type")
public class NotificationType {

    @Column(name="notification_type_id")
    @Id
    private Integer notificationTypeId;

    @Column(
            name="notification_type_name",
            unique=true,
            nullable = false
    )
    private String notificationTypeName;
}
