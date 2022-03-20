package com.example.demo.request;

import lombok.Data;

@Data
public class AccountAccessReq {

    private String accountId;

    private String userId;

    private Boolean access;

    private Boolean hidden;
}
