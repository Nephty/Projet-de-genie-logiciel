package com.example.demo.controller;


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
     * Returns an array with all the notifications for a certain user.
     *
     * <br>Http codes :
     * <ul>
     *     <li>200 - ok</li>
     *     <li>404 - Not found</li>
     * </ul>
     * Who ? the bank/user that made the request
     * @return An array of Notifications
     */
    @GetMapping
    public ResponseEntity<List<NotificationReq>> sendClientNotifications() {
        Sender sender = (Sender)httpRequest.getAttribute(Sender.getAttributeName());
        log.info("sender: {}", sender);

        return new ResponseEntity<>(notificationService.getNotifications(sender), HttpStatus.OK);
    }


    /**
     * add a Certain notification to the DB.
     *
     * <br>Http codes :
     * <ul>
     *     <li>201 - Created</li>
     *     <li>400 - Bad Request</li>
     *     <li>409 - Bad FK</li>
     * </ul>
     * Who ? user matching the notification id or bank depending on the destination
     *
     * @param notificationReq notification to add to the DB
     * @return notification to String
     */
    @PostMapping
    public ResponseEntity<String> addNotification(@RequestBody NotificationReq notificationReq) {
        if(!notificationReq.isPostValid()) throw new MissingParamException();

        Sender sender = (Sender)httpRequest.getAttribute(Sender.getAttributeName());
        Notification savedNotification = notificationService.addNotification(sender, notificationReq);
        return new ResponseEntity<>(savedNotification.toString(), HttpStatus.CREATED);
    }

    /**
     * Deletes a notification.
     *
     * <br>Http codes :
     * <ul>
     *     <li>200 - ok</li>
     * </ul>
     * Who ? user or bank that owns the notification
     *
     * @param id id of the notification to delete
     * @return notification with matching id
     */
    @DeleteMapping(value = "{id}")
    public ResponseEntity<String> deleteNotification(@PathVariable Integer id) {
        notificationService.deleteNotification(id);
        return new ResponseEntity<>(id.toString(), HttpStatus.OK);
    }


    /**
     * Modify a Notification.
     *
     * <br>Http codes.
     * <ul>
     *     <li>201 - Created</li>
     *     <li>400 - Bad Request</li>
     *     <li>404 - Not found</li>
     * </ul>
     * @param notificationReq notification to modify in the DB.
     * @return Notification to String
     */
    @PutMapping
    public ResponseEntity<String> changeNotification(
            @RequestBody NotificationReq notificationReq) {
        if(!notificationReq.isPutValid()) throw new MissingParamException();
        Notification savedNotification = notificationService.changeNotification(notificationReq);
        return new ResponseEntity<>(savedNotification.toString(),HttpStatus.CREATED);
    }
}
