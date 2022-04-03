package com.example.demo.repository;

import com.example.demo.model.CurrencyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
public interface CurrencyTypeRepo extends JpaRepository<CurrencyType,Integer> {

}
