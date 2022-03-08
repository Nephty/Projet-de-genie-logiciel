package com.example.demo.json;

import lombok.*;

import javax.persistence.Column;

@ToString
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AccountJson {
    private String iban;

    private String swift;

    private String userId;

    private int accountTypeId;

    private boolean payment;
}
