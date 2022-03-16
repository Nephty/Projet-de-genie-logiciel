package com.example.demo.repository;

import com.example.demo.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;

public interface NotificationRepo extends JpaRepository<Notification, String> {
}
