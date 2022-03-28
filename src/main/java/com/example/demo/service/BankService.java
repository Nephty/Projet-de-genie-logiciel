package com.example.demo.service;

import com.example.demo.exception.throwables.ConflictException;
import com.example.demo.exception.throwables.LittleBoyException;
import com.example.demo.exception.throwables.ResourceNotFound;
import com.example.demo.exception.throwables.UserAlreadyExist;
import com.example.demo.model.Bank;
import com.example.demo.model.CurrencyType;
import com.example.demo.model.User;
import com.example.demo.other.Sender;
import com.example.demo.repository.AccountAccessRepo;
import com.example.demo.repository.BankRepo;
import com.example.demo.repository.CurrencyTypeRepo;
import com.example.demo.request.BankReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service @Transactional @Slf4j
public class BankService {

    private final BankRepo bankRepo;

    private final PasswordEncoder passwordEncoder;

    private final CurrencyTypeRepo currencyTypeRepo;

    private final AccountAccessRepo accountAccessRepo;

    public Bank addBank(BankReq bankReq) {
        Bank bank = instantiateBank(null, bankReq, HttpMethod.POST);
        bank.setPassword(passwordEncoder.encode(bank.getPassword()));
        return bankRepo.save(bank);
    }

    public void deleteBank(String swift) {
        bankRepo.deleteById(swift);
    }

    public Bank changeBank(Sender sender,BankReq bankReq) {
        Bank bank = instantiateBank(sender, bankReq, HttpMethod.PUT);
        bank.setPassword(passwordEncoder.encode(bank.getPassword()));
        return bankRepo.save(bank);
    }

    public Bank getBank(String swift) {
        return bankRepo.findById(swift)
                .orElseThrow(()-> new ResourceNotFound(swift));
    }

    public ArrayList<Bank> getAllBanks() {
         return new ArrayList<>(bankRepo.findAll());
    }

    /**
     * Throws an error if the bank already exists in the DB
     * @param swift param that must not be unique
     * @param name param that must be unique
     * @throws UserAlreadyExist if there is already a bank with the same swift or name
     */
    private void alreadyExistCheck(String swift, String name) throws UserAlreadyExist {
        if(bankRepo.existsById(swift)) {
            throw new UserAlreadyExist(UserAlreadyExist.Reason.SWIFT);
        }
        if(bankRepo.existsByName(name)) {
            throw new UserAlreadyExist(UserAlreadyExist.Reason.NAME);
        }
    }

    public List<User> getAllCustomersOfABank(Sender sender){
        return accountAccessRepo.getAllCustomersInBank(sender.getId());
    }

    /**
     * Creates an entity based on the request that was made
     * The method vary depending on the http method
     * @param sender id and role of the client that made the request
     * @param bankReq incoming req
     * @param method method used either PUT or POST
     * @return An entity ready to be saved in the DB
     * @throws ConflictException if the FK provided is incorrect
     * @throws ResourceNotFound if the bank that the client tries to change doesn't exist
     * @throws LittleBoyException if the method isn't POST or PUT
     */
    private Bank instantiateBank(
            Sender sender,
            BankReq bankReq,
            HttpMethod method
    ) throws ConflictException, ResourceNotFound, LittleBoyException {
        Bank bank;
        CurrencyType currencyType;
        switch (method) {
            case POST:
                alreadyExistCheck(bankReq.getSwift(), bankReq.getName());
                bank = new Bank(bankReq);
                currencyType = currencyTypeRepo
                        .findById(bankReq.getDefaultCurrencyType())
                        .orElseThrow(() -> new ConflictException(bankReq.getDefaultCurrencyType().toString()));
                bank.setDefaultCurrencyType(currencyType);
                return bank;
            case PUT:
                bank = bankRepo.findById(sender.getId())
                        .orElseThrow(()-> new ResourceNotFound(sender.getId()));
                bank.change(bankReq);
                //alreadyExistCheck(bank.getSwift(), bank.getName());
                if(bankReq.getDefaultCurrencyType() != null) {
                    currencyType = currencyTypeRepo
                            .findById(bankReq.getDefaultCurrencyType())
                            .orElseThrow(() -> new ConflictException(bankReq.getDefaultCurrencyType().toString()));
                    bank.setDefaultCurrencyType(currencyType);
                }
                return bank;
            default:
                log.error("Invalid method {}", method);
                throw new LittleBoyException();
        }
    }
}
