package com.example.demo.scheduler;

import com.example.demo.model.Account;
import com.example.demo.model.AccountType;
import com.example.demo.model.SubAccount;
import com.example.demo.repository.AccountRepo;
import com.example.demo.repository.SubAccountRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@Component @RequiredArgsConstructor @Slf4j
public class AccountScheduler extends AbstractScheduler {

    private final AccountRepo accountRepo;
    private final SubAccountRepo subAccountRepo;

    @Scheduled(fixedRate = day, timeUnit = TimeUnit.SECONDS)
    public void processAccount() {
        log.info("[SCHEDULED]Started account processing");
        int[] nbAccounts = {0, 0};
        accountRepo.findAccountsToProcess().forEach(account -> {
            AccountType accountType = account.getAccountTypeId();
            if(accountType.getAnnualFee() != null && accountType.getAnnualFee() > 0) {
                nbAccounts[0]++;
                debitFee(account);
            }
            if(accountType.getAnnualReturn() != null && accountType.getAnnualReturn() > 0) {
                nbAccounts[1]++;
                addInterest(account);
            }
            updateAccount(account);
        });
        log.info("[SCHEDULED]Debited {} and topped {} account(s)", nbAccounts[0], nbAccounts[1]);
    }

    private void debitFee(Account account) {
        final double fee = account.getAccountTypeId().getAnnualFee();
        ArrayList<SubAccount> linkedSubAccount = subAccountRepo.findAllByIban(account);
        linkedSubAccount.forEach(subAccount ->
                subAccount.setCurrentBalance(
                        subAccount.getCurrentBalance() - subAccount.getCurrentBalance() * fee
        ));
        subAccountRepo.saveAll(linkedSubAccount);
    }

    private void addInterest(Account account) {
        final double interest = account.getAccountTypeId().getAnnualReturn();
        final int years = account.getAccountTypeId().getAccountTypeId() == 4 ? 5 : 1;
        ArrayList<SubAccount> linkedSubAccount = subAccountRepo.findAllByIban(account);
        linkedSubAccount.forEach(subAccount -> {
            subAccount.setCurrentBalance(
                    findNewAmount(subAccount.getCurrentBalance(), interest, years)
            );
        });
        subAccountRepo.saveAll(linkedSubAccount);
    }

    private void updateAccount(Account account) {
        Instant nextProcessInstant = account.getNextProcess()
                .toLocalDate()
                .plusYears(1)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant();

        account.setNextProcess(new Date(
                nextProcessInstant.toEpochMilli()
        ));
        accountRepo.save(account);
    }

    private Double findNewAmount(double currAmount, double interestRate, int years) {
        return Math.pow(((1+ interestRate) * currAmount), years);
    }
}
