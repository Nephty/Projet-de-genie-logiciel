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

import java.util.List;

@RequiredArgsConstructor
@Service @Transactional
public class NotificationService {

    private final NotificationRepo notificationRepo;

    private final NotificationTypeRepo notificationTypeRepo;

    public void addNotification(NotificationReq notificationReq) {
        Notification notification = instantiateNotification(notificationReq);
        notificationRepo.save(notification);
    }

    public void deleteNotification(Integer notificationId) {
        notificationRepo.deleteById(notificationId);
    }

    public Notification getNotification(Integer notificationId) {
        return notificationRepo.findById(notificationId)
                .orElseThrow(()-> new ResourceNotFound(notificationId.toString()));
    }

    public List<Notification> getUserNotification(String userId) {
        return null;
    }

    public List<Notification> getBankNotification(String swift) {
        return null;
    }

    private Notification instantiateNotification(NotificationReq notificationReq) {
        Notification notification = new Notification(notificationReq);
        NotificationType notificationType = notificationTypeRepo.findById(notificationReq.getNotificationType())
                .orElseThrow(()-> new ConflictException(notificationReq.getNotificationType().toString()));
        notification.setNotificationType(notificationType);
        return notification;
    }
}
