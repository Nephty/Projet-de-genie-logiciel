package com.example.demo.service;

import com.example.demo.exception.throwables.ConflictException;
import com.example.demo.exception.throwables.LittleBoyException;
import com.example.demo.exception.throwables.ResourceNotFound;
import com.example.demo.model.Bank;
import com.example.demo.model.Notification;
import com.example.demo.model.NotificationType;
import com.example.demo.model.User;
import com.example.demo.other.Sender;
import com.example.demo.repository.BankRepo;
import com.example.demo.repository.NotificationRepo;
import com.example.demo.repository.NotificationTypeRepo;
import com.example.demo.repository.UserRepo;
import com.example.demo.request.NotificationReq;
import com.example.demo.security.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service @Transactional @Slf4j
public class NotificationService {

    private final NotificationRepo notificationRepo;

    private final NotificationTypeRepo notificationTypeRepo;

    private final UserRepo userRepo;

    private final BankRepo bankRepo;

    public Notification addNotification(Sender sender, NotificationReq notificationReq) {
        Notification notification = instantiateNotification(sender, notificationReq);
        return notificationRepo.save(notification);
    }

    public void deleteNotification(Integer notificationId) {
        notificationRepo.deleteById(notificationId);
    }

    public List<NotificationReq> getUserNotification(String userId) {
        User user = userRepo.findById(userId).orElseThrow(()-> {
            log.error("no user with such id:" + userId);
            return new ResourceNotFound("no user with such id:" + userId);
        });
        ArrayList<Notification> notifications = notificationRepo.findAllByUserId(user);

        return formatResponse(notifications);
    }

    public List<NotificationReq> getBankNotification(String swift) {
        Bank bank = bankRepo.findById(swift).orElseThrow(()-> {
            log.error("no bank with such id: " + swift);
            return new ResourceNotFound("no bank with such id: " + swift);
        });
        ArrayList<Notification> notifications = notificationRepo.findAllByBankId(bank);

        return formatResponse(notifications);
    }

    private ArrayList<NotificationReq> formatResponse(ArrayList<Notification> notifications) {
        ArrayList<NotificationReq> response = new ArrayList<>();
        notifications.forEach(notification -> {
            if(!notification.getToBank()) {
                NotificationReq notificationReq = new NotificationReq(notification);
                response.add(notificationReq);
            }
        });
        return response;
    }

    private Notification instantiateNotification(Sender sender, NotificationReq notificationReq) {

        Notification notification = new Notification(notificationReq);
        NotificationType notificationType = notificationTypeRepo.findById(notificationReq.getNotificationType())
                .orElseThrow(()-> new ConflictException(notificationReq.getNotificationType().toString()));
        notification.setNotificationType(notificationType);

        boolean toBank;
        String swift;
        String userId;
        switch (sender.getRole()) {
            case USER:
                swift = notificationReq.getRecipientId();
                userId = sender.getId();
                toBank = true;
                break;
            case BANK:
                swift = sender.getId();
                userId = notificationReq.getRecipientId();
                toBank = false;
                break;
            default:
                log.error("unknown role " + sender);
                throw new LittleBoyException();

        }
        Bank bank = bankRepo.findById(swift).orElseThrow(()-> {
            log.error("no bank with such id: " + swift);
            return new ConflictException("no bank with such id: " + swift);
        });
        User user = userRepo.findById(userId).orElseThrow(()-> {
            log.error("no user with such id:" + userId);
            return new ConflictException("no user with such id: " + userId);
        });

        notification.setBankId(bank);
        notification.setUserId(user);
        notification.setToBank(toBank);
        return notification;
    }
}
