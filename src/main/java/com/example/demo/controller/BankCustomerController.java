package com.example.demo.controller;

import com.example.demo.model.BankCustomers;
import com.example.demo.service.BankCustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RequiredArgsConstructor
@RequestMapping(value = "/api/bank/customer")
@RestController
public class BankCustomerController {

    private final BankCustomerService bankCustomerService;

    @PostMapping
    public void addCustomer(@RequestParam String swift, @RequestParam String userId) {
        bankCustomerService.addCustomer(swift, userId);
    }

    @GetMapping("{swift}")
    public ArrayList<BankCustomers> getCustomers(@PathVariable String swift) {
        return bankCustomerService.getCustomers(swift);
    }

    @DeleteMapping
    public void deleteCustomer(@RequestParam String swift, @RequestParam("user-id") String userId) {
        bankCustomerService.deleteCustomer(swift, userId);
    }
}
