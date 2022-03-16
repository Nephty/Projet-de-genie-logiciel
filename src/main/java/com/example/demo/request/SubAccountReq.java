package com.example.demo.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class SubAccountReq {

    private String iban;

    private Integer currencyType;

    private Double currentBalance;

}