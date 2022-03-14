package com.example.demo.controller;

import com.example.demo.exception.throwables.UnimplementedException;
import com.example.demo.model.TransactionLog;
import com.example.demo.service.TransactionLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Optional;

@RequiredArgsConstructor
@RequestMapping(value = "/api/transaction")
@RestController
public class TransactionController {

    private final TransactionLogService transactionLogService;

    @PostMapping
    public ResponseEntity<String> makeTransfer(@RequestBody TransactionLog transactionLog) {
        transactionLogService.addTransaction(transactionLog);
        return new ResponseEntity<>(transactionLog.toString(), HttpStatus.CREATED);
    }

    @GetMapping(value = "{iban}")
    public ResponseEntity<TransactionLog> sendTransfer(@PathVariable String iban) {
        return new ResponseEntity<>(transactionLogService.getTransactionByIban(iban), HttpStatus.OK);
    }
}
