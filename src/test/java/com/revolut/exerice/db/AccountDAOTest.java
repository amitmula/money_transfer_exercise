package com.revolut.exerice.db;

import com.revolut.exerice.core.Account;
import io.dropwizard.testing.junit5.DAOTestExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@ExtendWith(DropwizardExtensionsSupport.class)
public class AccountDAOTest {

    public DAOTestExtension daoTestRule = DAOTestExtension.newBuilder()
        .addEntityClass(Account.class)
        .build();

    private AccountDAO accountDAO;

    @BeforeEach
    public void setUp() {
        accountDAO = new AccountDAO(daoTestRule.getSessionFactory());
    }

    @Test
    public void createAccount() {
        final Account account = daoTestRule.inTransaction(() -> accountDAO.create(new Account(BigDecimal.valueOf(4000.34))));
        assertThat(account.getId()).isGreaterThan(0);
        assertThat(account.getBalance()).isEqualTo(BigDecimal.valueOf(4000.34));
        assertThat(accountDAO.findAll().size() == 1);
    }

    @Test
    public void findAll() {
        daoTestRule.inTransaction(() -> {
            accountDAO.create(new Account(BigDecimal.valueOf(3400)));
            accountDAO.create(new Account(BigDecimal.valueOf(3500)));
            accountDAO.create(new Account(BigDecimal.valueOf(3600)));
        });

        final List<Account> accounts = accountDAO.findAll();
        assertThat(accounts).extracting("balance").containsOnly(BigDecimal.valueOf(3400), BigDecimal.valueOf(3500), BigDecimal.valueOf(3600));
    }

    @Test
    public void handlesNullBalance() {
        assertThatExceptionOfType(ConstraintViolationException.class).isThrownBy(()->
            daoTestRule.inTransaction(() -> accountDAO.create(new Account(null))));
    }
}
