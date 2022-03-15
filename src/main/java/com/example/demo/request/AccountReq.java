package com.example.demo.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class AccountReq {

    private String iban;

    private String swift;

    private String userId;

    private Integer accountTypeId;

    private Boolean payment;
}
