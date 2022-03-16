package com.example.demo.controller;


import com.example.demo.exception.throwables.UnimplementedException;
import com.example.demo.model.Notification;
import com.example.demo.request.NotificationReq;
import com.example.demo.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/notification")
@RestController
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * Returns an array with all the notifications for a certain user
     * @param userId id of the notification to retrieve
     * @return notification with matching id
     * 200 - Ok
     * 404 - Not found
     */
    @GetMapping(value = "/user/{userId}")
    public ResponseEntity<List<Notification>> sendUserNotification(@PathVariable String userId) {
       throw new UnimplementedException();
    }

    @GetMapping(value = "/bank/{swift}")
    public ResponseEntity<List<Notification>> sendBankNotification(@PathVariable String swift) {
        throw new UnimplementedException();
    }


    /**
     * @param notificationReq notification to add to the DB
     * @return notification to String
     * 201 - Created
     * 400 - Bad request
     * 409 - Bad FK
     */
    @PostMapping
    public ResponseEntity<String> addNotification(@RequestBody NotificationReq notificationReq) {
        notificationService.addNotification(notificationReq);
        return new ResponseEntity<>(notificationReq.toString(), HttpStatus.CREATED);
    }

    /**
     * @param id id of the notification to delete
     * @return notification with matching id
     * 200 - Ok
     */
    @DeleteMapping(value = "{id}")
    public ResponseEntity<String> deleteNotification(@PathVariable String id) {
        notificationService.deleteNotification(id);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }
}
