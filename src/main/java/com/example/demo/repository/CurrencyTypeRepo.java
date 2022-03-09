package com.example.demo.repository;

import com.example.demo.model.CurrencyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyTypeRepo extends JpaRepository<CurrencyType,Integer> {
}
