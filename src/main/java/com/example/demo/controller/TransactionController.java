package com.example.demo.controller;

import com.example.demo.exception.throwables.UnimplementedException;
import com.example.demo.model.TransactionLog;
import com.example.demo.service.TransactionLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RequiredArgsConstructor
@RequestMapping(value = "/api/transaction")
@RestController
public class TransactionController {

    private final TransactionLogService transactionLogService;

    @PostMapping
    public void makeTransfer(@RequestBody TransactionLog transactionLog) {
        transactionLogService.addTransaction(transactionLog);
    }

    @GetMapping(value = "{iban}")
    public ArrayList<TransactionLog> sendTransfer(@PathVariable String iban) {
        return transactionLogService.getTransactionByIban(iban);
    }
}
