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

    /**
     * @param transactionLog [body] Transaction to send to the DB
     * @return Transaction to string in the response body
     * 201 - Transaction successfully created
     * 400 - Bad Format
     */
    @PostMapping
    public ResponseEntity<String> makeTransfer(@RequestBody TransactionLog transactionLog) {
        transactionLogService.addTransaction(transactionLog);
        return new ResponseEntity<>(transactionLog.toString(), HttpStatus.CREATED);
    }

    /**
     * Send a list of all transaction to and from a certain account
     * @param iban [path] id of the account
     * @return Array of transaction linked to an account
     * 200 - OK
     */
    @GetMapping(value = "{iban}")
    public ResponseEntity<TransactionLog> sendTransfer(@PathVariable String iban) {
        return new ResponseEntity<>(transactionLogService.getTransactionByIban(iban), HttpStatus.OK);
    }
}
