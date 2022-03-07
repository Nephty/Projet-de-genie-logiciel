package com.example.demo.model;

import com.example.demo.model.CompositePK.BanksCustomersPK;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@IdClass(BanksCustomersPK.class)
@Table(name="banks_customers")
public class BankCustomers {
    //Is this table really useful ??
    @Column(name="banks_swift")
    @Id
    private String bankSwift;

    @Column(name="customer_id")
    @Id
    private String customerId;
}
