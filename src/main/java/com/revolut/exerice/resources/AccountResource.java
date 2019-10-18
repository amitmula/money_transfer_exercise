package com.revolut.exerice.resources;

import com.revolut.exerice.core.Account;
import com.revolut.exerice.db.AccountDAO;
import io.dropwizard.hibernate.UnitOfWork;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Optional;

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
        return accountDAO.save(account);
    }

    @GET
    @UnitOfWork
    @Produces("application/json")
    public List<Account> listAccount() {
        return accountDAO.findAll();
    }

    @GET
    @Path("{id}")
    @UnitOfWork
    @Produces("application/json")
    public Optional<Account> getAccount(@PathParam("id") String id) {
        return accountDAO.findById(Long.valueOf(id));
    }

}
