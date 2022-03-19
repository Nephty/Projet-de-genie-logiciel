package com.example.demo.request;

import lombok.Data;

import java.sql.Date;

@Data
public class TransactionReq {

    private Integer transactionId;

    private Integer transactionTypeId;

    private Date transactionDate;

    private String iban;

    private Integer currencyId;

    private Double transactionAmount;

    private Integer direction;
}
