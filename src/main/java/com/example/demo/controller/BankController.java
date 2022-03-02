package com.example.demo.controller;

import com.example.demo.exception.throwables.UnimplementedException;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/bank")
@RestController
public class BankController {
    @PostMapping
    public void addBank() {
        throw new UnimplementedException();
    }
    @DeleteMapping(value = "{swift}")
    public void deleteBank(@PathVariable String swift) {
        throw new UnimplementedException();
    }

    @GetMapping(value = "{swift}")
    public void sendBank(@PathVariable String swift) {
        throw new UnimplementedException();
    }

    @PutMapping
    public void changeBank() {
        throw new UnimplementedException();
    }
}
