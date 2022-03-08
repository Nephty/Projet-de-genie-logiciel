package com.example.demo.repository;

import com.example.demo.model.AccountAccess;
import com.example.demo.model.CompositePK.AccountAccessPK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountAccessRepo extends JpaRepository<AccountAccess, AccountAccessPK> {

}
