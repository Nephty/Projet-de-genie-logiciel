package com.example.demo.repository;

import com.example.demo.model.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationTypeRepo extends JpaRepository<NotificationType, Integer> {
}
