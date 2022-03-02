package com.example.demo.controller;

import com.example.demo.exception.throwables.UnimplementedException;
import com.example.demo.model.User;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RequestMapping(value = "/api/bank/customer")
@RestController
public class BankCustomerController {
    @PostMapping
    public void addCustomer() {
        throw new UnimplementedException();
    }

    @GetMapping("{swift}")
    public Collection<User> getCustomers(@PathVariable String swift) {
        throw new UnimplementedException();
    }

    @DeleteMapping
    public void deleteCustomer(@RequestParam String swift, @RequestParam("user-id") String userId) {
        throw new UnimplementedException();
    }
}
