package com.example.demo.request;

import lombok.Data;

@Data
public class AccountReq {

    private String iban;

    private String swift;

    private String userId;

    private Integer accountTypeId;

    private Boolean payment;
}
