package com.example.demo.controller;


import com.example.demo.exception.throwables.LittleBoyException;
import com.example.demo.exception.throwables.UnimplementedException;
import com.example.demo.model.Notification;
import com.example.demo.other.Sender;
import com.example.demo.request.NotificationReq;
import com.example.demo.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/notification")
@RestController @Slf4j
public class NotificationController {

    private final NotificationService notificationService;

    private final HttpServletRequest httpRequest;

    /**
     * Returns an array with all the notifications for a certain user
     * @return An array of Notifications
     * 200 - OK
     * 404 - Not found
     * Who ? the bank/user that made the request
     */
    @GetMapping
    public ResponseEntity<List<NotificationReq>> sendClientNotifications() {
        Sender sender = (Sender)httpRequest.getAttribute(Sender.getAttributeName());
        switch (sender.getRole()) {
            case BANK:
                return new ResponseEntity<>(notificationService.getBankNotification(sender.getId()), HttpStatus.OK);
            case USER:
                return new ResponseEntity<>(notificationService.getUserNotification(sender.getId()), HttpStatus.OK);
            default:
                log.error("unknown role: " + sender);
                throw new LittleBoyException();
        }

    }


    /**
     * @param notificationReq notification to add to the DB
     * @return notification to String
     * 201 - Created
     * 400 - Bad request
     * 409 - Bad FK
     * Who ? user matching the notification id or bank depending on the destination
     * What ? /
     */
    @PostMapping
    public ResponseEntity<String> addNotification(@RequestBody NotificationReq notificationReq) {
        Sender sender = (Sender)httpRequest.getAttribute(Sender.getAttributeName());
        Notification savedNotification = notificationService.addNotification(sender, notificationReq);
        return new ResponseEntity<>(savedNotification.toString(), HttpStatus.CREATED);
    }

    /**
     * @param id id of the notification to delete
     * @return notification with matching id
     * 200 - Ok
     * Who ? user or bank that owns the notification
     * What ? /
     */
    @DeleteMapping(value = "{id}")
    public ResponseEntity<String> deleteNotification(@PathVariable String id) {
        notificationService.deleteNotification(Integer.getInteger(id));
        return new ResponseEntity<>(id, HttpStatus.OK);
    }
}
