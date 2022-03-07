package com.example.demo.model;

import com.example.demo.model.CompositePK.SubAccountPK;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@IdClass(SubAccountPK.class)
@Table(name="sub_account")
public class SubAccount {

    @Column @Id
    private String iban;
    @Column(name="currency_type_id")
    private int currencyTypeId;
    @Column(name="current_balance")
    private double currentBalance;

}
