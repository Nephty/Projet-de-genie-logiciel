package com.example.demo.controller;

import com.example.demo.exception.throwables.MissingParamException;
import com.example.demo.model.Bank;
import com.example.demo.model.User;
import com.example.demo.other.Sender;
import com.example.demo.request.BankReq;
import com.example.demo.service.BankService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping(path="/api/bank")
@RestController @Slf4j
public class BankController {

    private final BankService bankService;

    private final HttpServletRequest httpRequest;

    /**
     * @param bankReq bank to be added to the DB
     * @return bank to string
     * 201- Created
     * 400 - Bad Format
     * 409 - Conflict
     */
    @PostMapping
    public ResponseEntity<String> addBank(@RequestBody BankReq bankReq) {
        if(!bankReq.isPostValid()) throw new MissingParamException();

        Bank savedBank = bankService.addBank(bankReq);
        return new ResponseEntity<>(savedBank.toString(), HttpStatus.CREATED);
    }

    /**
     * @param swift id of the bank to retrieve
     * @return the bank with the matching id
     * 200 - OK
     * 404 - Not found
     */
    @GetMapping(value = "{swift}")
    public ResponseEntity<BankReq> sendBank(@PathVariable String swift) {
        return new ResponseEntity<>(bankService.getBank(swift), HttpStatus.OK);
    }

    /**
     * Sends all the banks in the bank table
     * @return Array with all banks
     * 200 - OK
     */
    @GetMapping
    public ResponseEntity<List<Bank>> sendAllBanks(){
        return new ResponseEntity<>(bankService.getAllBanks(), HttpStatus.OK);
    }

    /**
     * The sender must be a bank and the changes will be applied on the bank with the id matching the one on the token
     * @param bankReq bank to be changed in the DB
     * @return saved bank to String
     * 201 - Created
     * 400 - Bad Format
     * 404 - Not Found
     * 409 - Bad FK
     */
    @PutMapping
    public ResponseEntity<String> changeBank(@RequestBody BankReq bankReq) {
        if(!bankReq.isPutValid()) throw new MissingParamException();

        Bank savedBank = bankService.changeBank(
                (Sender)httpRequest.getAttribute(Sender.getAttributeName()),
                bankReq
        );
        return new ResponseEntity<>(
                savedBank.toString(),
                HttpStatus.CREATED
        );
    }

    /**
     * Returns all the customers with an account in the bank matching the token id
     * @return List of Users
     */
    @GetMapping("/customer")
    public ResponseEntity<List<User>> getAllBankCustomers() {
        Sender sender = (Sender)httpRequest.getAttribute(Sender.getAttributeName());
        return new ResponseEntity<>(
                bankService.getAllCustomersOfABank(sender),
                HttpStatus.OK
        );
    }
}
