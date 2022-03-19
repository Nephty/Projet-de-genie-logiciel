package com.example.demo.request;

import lombok.Data;

@Data
public class TransactionReq {

    private Integer transactionTypeId;

    private String senderIban;

    private String recipientIban;

    private Integer currencyId;

    private Double transactionAmount;
}
