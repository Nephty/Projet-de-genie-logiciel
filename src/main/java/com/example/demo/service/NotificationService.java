package com.example.demo.service;

import com.example.demo.exception.throwables.ConflictException;
import com.example.demo.exception.throwables.ResourceNotFound;
import com.example.demo.model.Notification;
import com.example.demo.model.NotificationType;
import com.example.demo.repository.NotificationRepo;
import com.example.demo.repository.NotificationTypeRepo;
import com.example.demo.request.NotificationReq;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service @Transactional
public class NotificationService {

    private final NotificationRepo notificationRepo;

    private final NotificationTypeRepo notificationTypeRepo;

    public void addNotification(NotificationReq notificationReq) {
        Notification notification = instantiateNotification(notificationReq);
        notificationRepo.save(notification);
    }

    public void deleteNotification(String notificationId) {
        notificationRepo.deleteById(notificationId);
    }

    public Notification getNotification(String notificationId) {
        return notificationRepo.findById(notificationId)
                .orElseThrow(()-> new ResourceNotFound(notificationId));
    }

    private Notification instantiateNotification(NotificationReq notificationReq) {
        Notification notification = new Notification(notificationReq);
        NotificationType notificationType = notificationTypeRepo.findById(notificationReq.getNotificationType())
                .orElseThrow(()-> new ConflictException(notificationReq.getNotificationType().toString()));
        notification.setNotificationType(notificationType);
        return notification;
    }
}
