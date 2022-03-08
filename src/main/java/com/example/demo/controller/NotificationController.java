package com.example.demo.controller;

import com.example.demo.exception.throwables.UnimplementedException;
import com.example.demo.model.Notification;
import com.example.demo.model.NotificationType;
import com.example.demo.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/notification")
@RestController
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping(value = "{id}")
    public Notification sendNotification(@PathVariable String id) {
        return notificationService.getNotification(id);
    }

    @PostMapping
    public void addNotification(@RequestBody Notification notification) {
        notificationService.addNotification(notification);
    }

    @DeleteMapping(value = "{id}")
    public void deleteNotification(@PathVariable String id) {
        notificationService.deleteNotification(id);
    }
}
