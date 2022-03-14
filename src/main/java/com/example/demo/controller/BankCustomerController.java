package com.example.demo.controller;

import com.example.demo.model.BankCustomers;
import com.example.demo.service.BankCustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RequiredArgsConstructor
@RequestMapping(value = "/api/bank/customer")
@RestController
public class BankCustomerController {

    private final BankCustomerService bankCustomerService;

    @PostMapping
    public ResponseEntity<String> addCustomer(@RequestParam String swift, @RequestParam("user-id") String userId) {
        bankCustomerService.addCustomer(swift, userId);
        return new ResponseEntity<>(swift + " : " + userId, HttpStatus.CREATED);
    }

    @GetMapping("{swift}")
    public ResponseEntity<ArrayList<BankCustomers>> getCustomers(@PathVariable String swift) {
        return new ResponseEntity<>(bankCustomerService.getCustomers(swift), HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<String> deleteCustomer(@RequestParam String swift, @RequestParam("user-id") String userId) {
        bankCustomerService.deleteCustomer(swift, userId);
        return new ResponseEntity<>(swift + " : " + userId, HttpStatus.OK);
    }
}
