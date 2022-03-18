package com.example.demo.repository;

import com.example.demo.model.Account;
import com.example.demo.model.AccountAccess;
import com.example.demo.model.CompositePK.AccountAccessPK;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface AccountAccessRepo extends JpaRepository<AccountAccess, AccountAccessPK> {

    @Query("SELECT s FROM AccountAccess s, User u where u.userID = ?1 and s.userId = u.userID " +
            "order by s.accountId.swift.swift")
    List<AccountAccess> getAllByUserId(String userID);


    boolean existsByUserIdAndAccountId(User user,Account account);

    @Modifying
    @Query("DELETE FROM AccountAccess a WHERE a.accountId.iban = ?1 AND a.userId.userID = ?2")
    void deleteAccountAccessByAccountIdAndUserId(String accountId, String userId);

}

