package com.example.demo.model;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "users")
@Table(name = "users")
public class User {
    @Column @Id
    private String id;
    @Column
    private String name;
    @Column
    private String email;
    @Column
    private String password;
}
