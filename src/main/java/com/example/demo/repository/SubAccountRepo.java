package com.example.demo.repository;

import com.example.demo.model.Account;
import com.example.demo.model.CompositePK.SubAccountPK;
import com.example.demo.model.CurrencyType;
import com.example.demo.model.SubAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SubAccountRepo extends JpaRepository<SubAccount, SubAccountPK> {

    Optional<SubAccount> findByIbanAndCurrencyType(Account iban, CurrencyType currency);
}
