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


    @Id
    @ManyToOne(targetEntity = Account.class)
    @JoinColumn(name="iban")
    private Account iban;

    @Id
    @ManyToOne(targetEntity = CurrencyType.class)
    @JoinColumn(name="currency_type_id")
    private CurrencyType currencyTypeId;

    @Column(name="current_balance")
    private Double currentBalance;

}
