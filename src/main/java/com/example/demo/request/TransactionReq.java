package com.example.demo.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Date;

@Data @AllArgsConstructor
public class TransactionReq {

    private Integer transactionTypeId;

    private String senderIban;

    private String recipientIban;

    private Integer currencyId;

    private Double transactionAmount;

    //Only for response

    private String senderName;

    private String recipientName;

    private Date transactionDate;

    private String currencyName;

    private Integer transactionId;
}
