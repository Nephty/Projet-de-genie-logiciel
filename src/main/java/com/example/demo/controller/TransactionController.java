package com.example.demo.controller;

import com.example.demo.exception.throwables.UnimplementedException;
import org.springframework.web.bind.annotation.*;

@RequestMapping(value = "/api/transaction")
@RestController
public class TransactionController {
    @PostMapping
    public void makeTransfer() {
        throw new UnimplementedException();
    }
    @GetMapping(value = "{iban}")
    public void sendTransfer(@PathVariable String iban) {
        throw new UnimplementedException();
    }
}
