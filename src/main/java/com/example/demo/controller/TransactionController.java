package com.example.demo.controller;

import com.example.demo.exception.throwables.MissingParamException;
import com.example.demo.model.TransactionLog;
import com.example.demo.request.TransactionReq;
import com.example.demo.service.TransactionLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping(value = "/api/transaction")
@RestController @Slf4j
public class TransactionController {

    private final TransactionLogService transactionLogService;

    /**
     * @param transactionReq [body] Transaction to send to the DB
     * @return Transaction to string in the response body
     * 201 - Transaction successfully created
     * 400 - Bad Format
     * Who ? anyone who owns the account
     * What ? debit the money or return an error if there is not enough
     */
    @PostMapping
    public ResponseEntity<String> makeTransfer(@RequestBody TransactionReq transactionReq) {
        if(!transactionReq.isPostValid()) throw new MissingParamException();

        ArrayList<TransactionLog> savedTransaction = transactionLogService.addTransaction(transactionReq);
        return new ResponseEntity<>(savedTransaction.toString(), HttpStatus.CREATED);
    }

    /**
     * Send a list of all transaction to and from a certain sub account
     * @param iban [path] id of the account
     * @return Array of transaction linked to an account
     * 200 - OK
     * Who ? bank or user who owns the account
     */
    @GetMapping
    public ResponseEntity<List<TransactionReq>> sendTransfer(
            @RequestParam String iban,
            @RequestParam Integer currencyId) {

        if(iban == null || currencyId == null) {
            throw new MissingParamException("parameter iban and currencyId are mandatory");
        }
        return new ResponseEntity<>(transactionLogService.getAllTransactionBySubAccount(iban, currencyId), HttpStatus.OK);
    }
}
