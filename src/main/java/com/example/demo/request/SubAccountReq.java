package com.example.demo.request;

import lombok.Data;

@Data
public class SubAccountReq {

    private String iban;

    private Integer currencyType;

    private Double currentBalance;

}
