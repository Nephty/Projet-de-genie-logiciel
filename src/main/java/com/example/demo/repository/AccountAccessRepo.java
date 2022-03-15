package com.example.demo.repository;

import com.example.demo.model.Account;
import com.example.demo.model.AccountAccess;
import com.example.demo.model.CompositePK.AccountAccessPK;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AccountAccessRepo extends JpaRepository<AccountAccess, AccountAccessPK> {
    @Query("SELECT s FROM AccountAccess s, User u where u.userID = ?1 and s.userId = u.userID " +
            "order by s.accountId.swift.swift")
    List<AccountAccess> getAllByUserId(String userID);

    Optional<AccountAccess> getAccountAccessByUserIdAndAccountId(User userId,Account accountId);

    @Modifying
    @Query("DELETE FROM AccountAccess a WHERE a.accountId = ?1 AND a.userId = ?2")
    void deleteAccountAccessByAccountIdAndUserId(Account accountId, User userId);

}
