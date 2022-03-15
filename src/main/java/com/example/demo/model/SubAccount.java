package com.example.demo.model;

import com.example.demo.model.CompositePK.SubAccountPK;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="sub_account")
public class SubAccount {

    /*
    @Id
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(
            name="iban",
            referencedColumnName = "iban"
    )
    private Account iban;

    @Id
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(
            name="currency_type_id",
            referencedColumnName = "currency_type_id"
    )
    private CurrencyType currencyTypeId;
     */

    @EmbeddedId
    private SubAccountPK subAccountPK;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL)
    @MapsId("iban")
    @JoinColumn(
            name="iban",
            referencedColumnName = "iban"
    )
    private Account iban;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL)
    @MapsId("currencyTypeId")
    @JoinColumn(
            name="currency_type_id",
            referencedColumnName = "currency_type_id"
    )
    private CurrencyType currencyType;

    @Column(
            name="current_balance",
            nullable = false
    )
    private Double currentBalance;

    public SubAccount(SubAccountPK subAccountPK) {
        this.subAccountPK = subAccountPK;
    }
}
