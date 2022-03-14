package com.example.demo.controller;

import com.example.demo.exception.throwables.UnimplementedException;
import com.example.demo.model.Bank;
import com.example.demo.service.BankService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping(path="/api/bank")
@RestController
public class BankController {

    private final BankService bankService;

    @PostMapping
    public ResponseEntity<String> addBank(@RequestBody Bank bank) {
        bankService.addBank(bank);
        return new ResponseEntity<>(bank.toString(), HttpStatus.CREATED);
    }
    @DeleteMapping(value = "{swift}")
    public ResponseEntity<String> deleteBank(@PathVariable String swift) {
        bankService.deleteBank(swift);
        return new ResponseEntity<>(swift, HttpStatus.CREATED);
    }

    @GetMapping(value = "{swift}")
    public ResponseEntity<Bank> sendBank(@PathVariable String swift) {
        return new ResponseEntity<>(bankService.getBank(swift), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Bank>> sendAllBanks(){
        return new ResponseEntity<>(bankService.getAllBanks(), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<String> changeBank(@RequestBody Bank bank) {
        bankService.changeBank(bank);
        return new ResponseEntity<>(bank.toString(), HttpStatus.CREATED);
    }
}
