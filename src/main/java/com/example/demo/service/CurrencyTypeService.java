package com.example.demo.service;

import com.example.demo.model.CurrencyType;
import com.example.demo.repository.CurrencyTypeRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class CurrencyTypeService {
    private final CurrencyTypeRepo currencyTypeRepo;

    public CurrencyType getCurrencyById(Integer id){
        return currencyTypeRepo.findById(id)
                .orElseThrow(
                        ()->new EntityNotFoundException("currency with this id not found : "+id)
                );
    }
}
