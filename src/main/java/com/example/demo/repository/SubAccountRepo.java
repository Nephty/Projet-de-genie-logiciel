package com.example.demo.repository;

import com.example.demo.model.CompositePK.SubAccountPK;
import com.example.demo.model.SubAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubAccountRepo extends JpaRepository<SubAccount, SubAccountPK> {
}
