package com.example.demo.controller;

import com.example.demo.exception.throwables.UnimplementedException;
import com.example.demo.model.Notification;
import com.example.demo.model.NotificationType;
import com.example.demo.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/notification")
@RestController
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping(value = "{id}")
    public ResponseEntity<Notification> sendNotification(@PathVariable String id) {
        return new ResponseEntity<>(notificationService.getNotification(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<String> addNotification(@RequestBody Notification notification) {
        notificationService.addNotification(notification);
        return new ResponseEntity<>(notification.toString(), HttpStatus.CREATED);
    }

    @DeleteMapping(value = "{id}")
    public ResponseEntity<String> deleteNotification(@PathVariable String id) {
        notificationService.deleteNotification(id);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }
}
