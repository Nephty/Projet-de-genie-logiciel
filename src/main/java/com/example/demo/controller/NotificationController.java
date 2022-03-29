package com.example.demo.controller;


import com.example.demo.exception.throwables.LittleBoyException;
import com.example.demo.exception.throwables.MissingParamException;
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
        log.info("sender: {}", sender);

        return new ResponseEntity<>(notificationService.getNotifications(sender), HttpStatus.OK);
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
        if(!notificationReq.isPostValid()) throw new MissingParamException();

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
    public ResponseEntity<String> deleteNotification(@PathVariable Integer id) {
        notificationService.deleteNotification(id);
        return new ResponseEntity<>(id.toString(), HttpStatus.OK);
    }
}
