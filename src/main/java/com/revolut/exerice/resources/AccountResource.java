package com.revolut.exerice.resources;

import com.revolut.exerice.core.Account;
import com.revolut.exerice.db.AccountDAO;
import io.dropwizard.hibernate.UnitOfWork;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/account")
@Produces(MediaType.APPLICATION_JSON)
public class AccountResource {

    private final AccountDAO accountDAO;

    public AccountResource(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    @POST
    @UnitOfWork
    public Account createAccount(Account account) {
        return accountDAO.create(account);
    }

    @GET
    @UnitOfWork
    public List<Account> listAccount() {
        return accountDAO.findAll();
    }

}
