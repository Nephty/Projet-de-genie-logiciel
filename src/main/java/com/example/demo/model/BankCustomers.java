package com.example.demo.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="banks_customers")
public class BankCustomers {
    @Column(name="banks_swift") @Id
    private String bankSwift;

    @Column(name="customer_id")
    private String customerId;
    // TODO multiple primary keys
}
