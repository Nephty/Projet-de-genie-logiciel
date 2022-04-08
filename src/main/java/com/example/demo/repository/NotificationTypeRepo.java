package com.example.demo.repository;

import com.example.demo.model.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * All the db request on the {@link NotificationType} table.
 */
public interface NotificationTypeRepo extends JpaRepository<NotificationType, Integer> {
}
