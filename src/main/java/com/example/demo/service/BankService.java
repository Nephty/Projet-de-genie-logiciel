package com.example.demo.service;

import com.example.demo.controller.BankController;
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

/**
 * Links the {@link BankController} with the {@link BankRepo}.
 * In this class, all the modifications and the calls to the {@link BankRepo} are made.
 *
 * @see Bank
 */
@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class BankService {

    private final BankRepo bankRepo;

    private final PasswordEncoder passwordEncoder;

    private final CurrencyTypeRepo currencyTypeRepo;

    private final AccountAccessRepo accountAccessRepo;

    /**
     * Creates a Bank and saves it in the DB.
     * it also encodes the password with {@link PasswordEncoder}
     *
     * @param bankReq The req body to create a Bank {@link BankReq#isPostValid()}
     * @return The created bank.
     * @throws ConflictException  if the FK provided is incorrect
     * @throws UserAlreadyExist   if there is already a bank with the same swift or name
     * @throws LittleBoyException if the method isn't POST or PUT
     */
    public Bank addBank(BankReq bankReq) {
        Bank bank = instantiateBank(null, bankReq, HttpMethod.POST);
        bank.setPassword(passwordEncoder.encode(bank.getPassword()));
        return bankRepo.save(bank);
    }

    /**
     * Changes the bank (password or defaultCurrencyType)
     *
     * @param sender  The sender of the request
     * @param bankReq The req body to change a Bank {@link BankReq#isPutValid()}
     * @return The changed bank
     * @throws ConflictException  if the FK provided is incorrect
     * @throws ResourceNotFound   if the bank that the client tries to change doesn't exist
     * @throws LittleBoyException if the method isn't POST or PUT (unexpected)
     */
    public Bank changeBank(Sender sender, BankReq bankReq) {
        Bank bank = instantiateBank(sender, bankReq, HttpMethod.PUT);
        bank.setPassword(passwordEncoder.encode(bank.getPassword()));
        return bankRepo.save(bank);
    }

    /**
     * Find a bank with its swift.
     *
     * @param swift The swift of the bank.
     * @return Req body of the Bank with the good swift.
     * @throws ResourceNotFound if the bank doesn't exist.
     */
    public BankReq getBank(String swift) {
        Bank bank = bankRepo.findById(swift)
                .orElseThrow(() -> new ResourceNotFound(swift));
        return new BankReq(bank, false);
    }

    /**
     * Gets all banks stored in the DB.
     *
     * @return An ArrayList of banks.
     */
    public ArrayList<Bank> getAllBanks() {
        return new ArrayList<>(bankRepo.findAll());
    }

    /**
     * Gets all customers of a Bank.
     * A User is considered as a customer when he has access to an account in this Bank.
     *
     * @param sender The sender of the request
     * @return A list of User that are client of the bank.
     */
    public List<User> getAllCustomersOfABank(Sender sender) {
        return accountAccessRepo.getAllCustomersInBank(sender.getId());
    }

    /**
     * Throws an error if the bank already exists in the DB
     *
     * @param swift param that must not be unique
     * @param name  param that must be unique
     * @throws UserAlreadyExist if there is already a bank with the same swift or name
     */
    private void alreadyExistCheck(String swift, String name) throws UserAlreadyExist {
        if (bankRepo.existsById(swift)) {
            throw new UserAlreadyExist(UserAlreadyExist.Reason.SWIFT);
        }
        if (bankRepo.existsByName(name)) {
            throw new UserAlreadyExist(UserAlreadyExist.Reason.NAME);
        }
    }

    /**
     * Creates an entity based on the request that was made
     * The method vary depending on the http method
     *
     * @param sender  id and role of the client that made the request
     * @param bankReq incoming req
     * @param method  method used either PUT or POST
     * @return An entity ready to be saved in the DB
     * @throws ConflictException  if the FK provided is incorrect
     * @throws ResourceNotFound   if the bank that the client tries to change doesn't exist
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
                        .findById(bankReq.getDefaultCurrencyId())
                        .orElseThrow(() -> new ConflictException(bankReq.getDefaultCurrencyId().toString()));
                bank.setDefaultCurrencyType(currencyType);
                return bank;
            case PUT:
                bank = bankRepo.findById(sender.getId())
                        .orElseThrow(() -> new ResourceNotFound(sender.getId()));
                bank.change(bankReq);
                //alreadyExistCheck(bank.getSwift(), bank.getName());
                if (bankReq.getDefaultCurrencyId() != null) {
                    currencyType = currencyTypeRepo
                            .findById(bankReq.getDefaultCurrencyId())
                            .orElseThrow(() -> new ConflictException(bankReq.getDefaultCurrencyId().toString()));
                    bank.setDefaultCurrencyType(currencyType);
                }
                return bank;
            default:
                log.error("Invalid method {}", method);
                throw new LittleBoyException();
        }
    }
}
