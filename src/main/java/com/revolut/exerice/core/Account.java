package com.revolut.exerice.core;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "account")
@NamedQueries({
    @NamedQuery(
        name = "com.revolut.exerice.core.Account.findAll",
        query = "SELECT a FROM Account a"
    )
})
@Data
@NoArgsConstructor
@EqualsAndHashCode
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "balance", nullable = false)
    private BigDecimal balance;

    public Account(BigDecimal balance) {
        this.balance = balance;
    }

}
