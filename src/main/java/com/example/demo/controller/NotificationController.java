package com.example.demo.controller;

import com.example.demo.exception.throwables.UnimplementedException;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/notification")
@RestController
public class NotificationController {
    //WHAT parameter to find which notif
    @GetMapping
    public void sendNotification() {
        throw new UnimplementedException();
    }

    @PostMapping
    public void addNotification() {
        throw new UnimplementedException();
    }

    @DeleteMapping
    public void deleteNotification() {
        throw new UnimplementedException();
    }
}
