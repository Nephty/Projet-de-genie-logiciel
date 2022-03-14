package com.example.demo.repository;

import com.example.demo.model.AccountAccess;
import com.example.demo.model.CompositePK.AccountAccessPK;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AccountAccessRepo extends JpaRepository<AccountAccess, AccountAccessPK> {
    @Query("SELECT s FROM AccountAccess s, User u where u.userID = ?1 and s.userId = u.userID " +
            "group by s.accountId.swift")
    List<AccountAccess> getAllByUserId(String userID);
}
