package com.example.demo.controller;

import com.example.demo.exception.throwables.UnimplementedException;
import com.example.demo.model.Bank;
import com.example.demo.service.BankService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping(path="/api/bank")
@RestController @Slf4j
public class BankController {

    private final BankService bankService;

    /**
     * @param bank [body] Bank to be added to the DB
     * @return Bank to String in the body
     * 201 - Created
     * 400 - Bad format
     */
    @PostMapping
    public ResponseEntity<String> addBank(@RequestBody Bank bank) {
        log.info("incoming bank: {}", bank.toString());
        bankService.addBank(bank);
        return new ResponseEntity<>(bank.toString(), HttpStatus.CREATED);
    }

    /**
     * @param swift [path] id of the bank to be deleted
     * @return id sent
     * 200 - OK
     */
    @DeleteMapping(value = "{swift}")
    public ResponseEntity<String> deleteBank(@PathVariable String swift) {
        bankService.deleteBank(swift);
        return new ResponseEntity<>(swift, HttpStatus.CREATED);
    }

    /**
     * @param swift [path] id of the bank to retrieve
     * @return bank with matching id
     * 200 -OK
     * 404- Resource not found
     */
    @GetMapping(value = "{swift}")
    public ResponseEntity<Bank> sendBank(@PathVariable String swift) {
        return new ResponseEntity<>(bankService.getBank(swift), HttpStatus.OK);
    }

    /**
     * @return A list with all banks
     * 200 - OK
     */
    @GetMapping
    public ResponseEntity<List<Bank>> sendAllBanks(){
        return new ResponseEntity<>(bankService.getAllBanks(), HttpStatus.OK);
    }

    /**
     * @param bank [body] Bank to change in the DB
     * @return bank to String
     * 201 - CREATED
     * 400 - Bad format
     */
    @PutMapping
    public ResponseEntity<String> changeBank(@RequestBody Bank bank) {
        bankService.changeBank(bank);
        return new ResponseEntity<>(bank.toString(), HttpStatus.CREATED);
    }
}
