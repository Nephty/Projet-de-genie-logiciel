package com.example.demo.service;

import com.example.demo.exception.throwables.ResourceNotFound;
import com.example.demo.model.Bank;
import com.example.demo.repository.BankRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class BankService {

    private final BankRepo bRepo;

    public Bank getBankBySwift(String swift) {
        return bRepo.findById(swift).orElseThrow(()->
                new ResourceNotFound("No bank with this swift:" + swift));
    }

    // TODO Add pagination to the getAll
    public List<Bank> getAllBanks(){
        return bRepo.findAll();
    }

}
