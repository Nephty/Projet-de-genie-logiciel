package com.example.demo.repository;

import com.example.demo.model.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountTypeRepo extends JpaRepository<AccountType, Integer> {
}
