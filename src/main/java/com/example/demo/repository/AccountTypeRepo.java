package com.example.demo.repository;

import com.example.demo.model.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;

@Transactional
public interface AccountTypeRepo extends JpaRepository<AccountType, Integer> {
}
