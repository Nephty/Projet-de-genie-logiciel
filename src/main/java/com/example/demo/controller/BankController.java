package com.example.demo.controller;

import com.example.demo.exception.throwables.UnimplementedException;
import com.example.demo.model.Bank;
import com.example.demo.service.BankService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping(path="/api/bank")
@RestController
public class BankController {

    private final BankService bankService;

    @PostMapping
    public void addBank() {
        //TODO post method
        /*
            Solution to the problem : maybe create the Bank class step by step
            (fetching CurrencyType by id and add it in the Bank instance)
        */
        throw new UnimplementedException();
    }
    @DeleteMapping(value = "{swift}")
    public void deleteBank(@PathVariable String swift) {
        throw new UnimplementedException();
    }

    @GetMapping(value = "{swift}")
    public Bank sendBank(@PathVariable String swift) {
        return bankService.getBankBySwift(swift);
    }

    @GetMapping
    public List<Bank> sendAllBanks(){
        return bankService.getAllBanks();
    }

    @PutMapping
    public void changeBank() {
        throw new UnimplementedException();
    }
}
