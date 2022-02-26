package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "account_access")
//@Table("account_access")
public class AccountAccess {
    @Id @Column
    private String accountId;
    @Column
    private String userId;
    @Column
    private boolean access;
    @Column
    private boolean hidden;
}
