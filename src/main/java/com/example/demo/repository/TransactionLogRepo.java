package com.example.demo.repository;

import com.example.demo.model.SubAccount;
import com.example.demo.model.TransactionLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.Optional;

public interface TransactionLogRepo extends JpaRepository<TransactionLog, Integer> {
    Optional<TransactionLog> findAllBySubAccount(SubAccount subAccount);

}
