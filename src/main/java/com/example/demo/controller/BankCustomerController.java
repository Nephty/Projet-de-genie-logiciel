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

    /**
     * @param swift [param] Bank to which the customer belongs
     * @param userId [param] Customer of the bank
     * @return parameter sent in the body response
     * 201 - CREATED
     */
    @PostMapping
    public ResponseEntity<String> addCustomer(@RequestParam String swift, @RequestParam("user-id") String userId) {
        bankCustomerService.addCustomer(swift, userId);
        return new ResponseEntity<>(swift + " : " + userId, HttpStatus.CREATED);
    }

    /**
     * Returns a list of all customers attached to a bank
     * @param swift [path] Bank to which the customer belongs
     * @return Array of Customer
     * 200 - OK
     */
    //TODO Format the result from DB [SERIOUS]
    @GetMapping("{swift}")
    public ResponseEntity<ArrayList<BankCustomers>> getCustomers(@PathVariable String swift) {
        return new ResponseEntity<>(bankCustomerService.getCustomers(swift), HttpStatus.OK);
    }

    /**
     * @param swift [param] id of the bank to which the customer belongs
     * @param userId [param] id of the user
     * @return detach the customer from that bank
     * 200 - OK
     */
    @DeleteMapping
    public ResponseEntity<String> deleteCustomer(@RequestParam String swift, @RequestParam("user-id") String userId) {
        bankCustomerService.deleteCustomer(swift, userId);
        return new ResponseEntity<>(swift + " : " + userId, HttpStatus.OK);
    }
}
