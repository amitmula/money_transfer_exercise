package com.revolut.exerice.db;

import com.revolut.exerice.core.Account;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.List;

public class AccountDAO extends AbstractDAO<Account> {
    public AccountDAO(SessionFactory factory) {
        super(factory);
    }

    public Account create(Account account) {
        return persist(account);
    }

    @SuppressWarnings("unchecked")
    public List<Account> findAll() {
        return list((Query<Account>) namedQuery("com.revolut.exerice.core.Account.findAll"));
    }
}
