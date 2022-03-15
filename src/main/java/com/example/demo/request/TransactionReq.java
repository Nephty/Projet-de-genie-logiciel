package com.example.demo.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data @AllArgsConstructor
public class TransactionReq {

    private Integer transactionId;

    private Integer transactionTypeId;

    private Date transactionDate;

    private String iban;

    private Integer currencyId;

    private Double transactionAmount;

    private Integer direction;
}
