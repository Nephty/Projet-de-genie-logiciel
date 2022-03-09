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
    public void addBank(@RequestBody Bank bank) {
       bankService.addBank(bank);
    }
    @DeleteMapping(value = "{swift}")
    public void deleteBank(@PathVariable String swift) {
        bankService.deleteBank(swift);
    }

    @GetMapping(value = "{swift}")
    public Bank sendBank(@PathVariable String swift) {
        return bankService.getBank(swift);
    }

    @GetMapping
    public List<Bank> sendAllBanks(){
        return bankService.getAllBanks();
    }

    @PutMapping
    public void changeBank(@RequestBody Bank bank) {
        bankService.changeBank(bank);
    }
}
