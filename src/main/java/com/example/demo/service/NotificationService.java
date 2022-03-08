package com.example.demo.service;

import com.example.demo.model.Notification;
import com.example.demo.repository.NotificationRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service @Transactional
public class NotificationService {

    private final NotificationRepo notificationRepo;

    public void addNotification(Notification notification) {
        notificationRepo.save(notification);
    }

    public void deleteNotification(String notificationId) {
        notificationRepo.deleteById(notificationId);
    }

    public Notification getNotification(String notificationId) {
        return notificationRepo.getById(notificationId);
    }
}
