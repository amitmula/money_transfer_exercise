package com.revolut.exerice.db;

import com.revolut.exerice.core.Account;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

public class AccountDAO extends AbstractDAO<Account> {

    public AccountDAO(SessionFactory factory) { super(factory); }

    public Account save(Account account) {
        return persist(account);
    }

    public Optional<Account> findById(Long id) {
        return Optional.ofNullable(get(id));
    }

    @SuppressWarnings("unchecked")
    public List<Account> findAll() {
        return list((Query<Account>) namedQuery("com.revolut.exerice.core.Account.findAll"));
    }
}
