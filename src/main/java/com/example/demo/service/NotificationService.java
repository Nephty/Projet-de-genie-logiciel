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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    public List<NotificationReq> getNotifications(Sender sender) {
        ArrayList<Notification> notifications;
        switch (sender.getRole()) {
            case BANK:
                Bank bank = bankRepo.findById(sender.getId()).orElseThrow(()-> {
                    log.warn("no bank with such id: " + sender.getId());
                    return new ResourceNotFound("no bank with such id: " + sender.getId());
                });
                 notifications = notificationRepo.findAllByBankId(bank);

                return formatResponse(notifications, sender.getRole());
            case USER:
                User user = userRepo.findById(sender.getId()).orElseThrow(()-> {
                    log.error("no user with such id:" + sender.getId());
                    return new ResourceNotFound("no user with such id: " + sender.getId());
                });
                notifications = notificationRepo.findAllByUserId(user);
                log.info("fetching notifications for user: {}", notifications.size());
                return formatResponse(notifications, sender.getRole());
            default:
                log.error("unknown role: " + sender);
                throw new LittleBoyException();
        }
    }

    /**
     * @param notifications array of Notification entity fresh from the DB
     * @return formatted array with unnecessary data removed
     */
    private ArrayList<NotificationReq> formatResponse(ArrayList<Notification> notifications, Role role) {
        ArrayList<NotificationReq> response = new ArrayList<>();

        notifications.forEach(notification -> {
            log.info("test: {}", role == Role.USER && !notification.getToBank());
            if(role == Role.USER && !notification.getToBank()) {
                response.add(new NotificationReq(notification));
            }
            if(role == Role.BANK && notification.getToBank()) {
                response.add(new NotificationReq(notification));
            }
        });
        return response;
    }

    /**
     * @param sender id and role of the client
     * @param notificationReq request made by the client
     * @return Notification entity to be added to the DB
     * @throws ConflictException if the FK provided by the client are incorrect
     * @throws LittleBoyException if the client role is not USER or BANK
     */
    private Notification instantiateNotification(
            Sender sender,
            NotificationReq notificationReq
    ) throws ConflictException, LittleBoyException {

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
