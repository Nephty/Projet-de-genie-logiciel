package com.example.demo.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class AccountAccessReq {

    private String accountId;

    private String userId;

    private Boolean access;

    private Boolean hidden;
}
