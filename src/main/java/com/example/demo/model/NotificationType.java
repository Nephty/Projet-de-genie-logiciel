package com.example.demo.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="notification_type")
public class NotificationType {

    @Column(name="notification_type_id") @Id
    private int notificationTypeId;
    @Column(name="notification_type_name",unique=true)
    private String notificationTypeName;
}
