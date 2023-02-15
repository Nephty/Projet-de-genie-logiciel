package com.example.demo.controller;

import com.example.demo.exception.throwables.MissingParamException;
import com.example.demo.model.TransactionLog;
import com.example.demo.other.Sender;
import com.example.demo.request.TransactionReq;
import com.example.demo.service.TransactionLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping(value = "/api/transaction")
@RestController
@Slf4j
public class TransactionController {

    private final TransactionLogService transactionLogService;

    private final HttpServletRequest httpRequest;

    /**
     * Debit the money or return an error if there is not enough.
     *
     * <br>Http codes :
     * <ul>
     *     <li>201 - created</li>
     *     <li>400 - Bad format</li>
     *     <li>403 - Bad Authorisation</li>
     *     <li>409 - Conflict</li>
     * </ul>
     * Who ? anyone who owns the account
     *
     * @param transactionReq [body] Transaction to send to the DB
     * @return Transaction to string in the response body
     */
    @PostMapping
    public ResponseEntity<String> makeTransfer(@RequestBody TransactionReq transactionReq) {
        log.info("insert transaction req: {}", transactionReq);
        if (!transactionReq.isPostValid()) throw new MissingParamException();

        Sender sender = (Sender) httpRequest.getAttribute(Sender.getAttributeName());
        ArrayList<TransactionLog> savedTransaction = transactionLogService.addTransaction(
                sender,
                transactionReq
        );
        return new ResponseEntity<>(savedTransaction.toString(), HttpStatus.CREATED);
    }

    /**
     * Send a list of all transaction to and from a certain SubAccount.
     *
     * <br>Http codes :
     * <ul>
     *     <li>200 - ok</li>
     *     <li>400 - Bad format</li>
     *     <li>404 - Not found</li>
     * </ul>
     * Who ? bank or user who owns the account
     *
     * @param iban [path] id of the account
     * @return Array of transaction linked to an account
     */
    @GetMapping
    public ResponseEntity<List<TransactionReq>> sendTransfer(
            @RequestParam String iban,
            @RequestParam Integer currencyId) {
        return new ResponseEntity<>(transactionLogService.getAllTransactionBySubAccount(iban, currencyId), HttpStatus.OK);
    }
}
