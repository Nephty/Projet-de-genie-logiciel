package com.example.demo.request;

import com.example.demo.model.TransactionLog;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data @AllArgsConstructor @NoArgsConstructor
public class TransactionReq {

    private Integer transactionTypeId;

    private String senderIban;

    private String recipientIban;

    private Integer currencyId;

    private Double transactionAmount;

    private Date transactionDate;

    private String comments;

    //Only for response

    private String senderName;

    private String recipientName;

    private String currencyName;

    private Integer transactionId;

    private Boolean processed;

    @JsonIgnore
    public boolean isPostValid() {
        return transactionTypeId != null
                && senderIban != null
                && recipientIban != null
                && currencyId != null
                && transactionAmount != null
                && comments != null
                && processed == null;
    }

    public TransactionReq(TransactionLog transactionSent, TransactionLog transactionReceived) {
        transactionTypeId = transactionSent.getTransactionTypeId().getTransactionTypeId();
        senderIban = transactionSent.getSubAccount().getIban().getIban();
        recipientIban = transactionReceived.getSubAccount().getIban().getIban();
        currencyId = transactionSent.getSubAccount().getCurrencyType().getCurrencyId();
        transactionAmount = transactionReceived.getTransactionAmount();
        senderName = transactionSent.getSubAccount().getIban().getUserId().getFullName();
        recipientName = transactionReceived.getSubAccount().getIban().getUserId().getFullName();
        transactionDate = transactionReceived.getTransactionDate();
        currencyName = transactionReceived.getSubAccount().getCurrencyType().getCurrencyTypeName();
        transactionId = transactionReceived.getTransactionId();
        comments = transactionSent.getComments();
        processed = transactionSent.getProcessed();
    }
}
