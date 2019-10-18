package com.revolut.exerice.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.revolut.exerice.core.Account;
import com.revolut.exerice.db.AccountDAO;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.dropwizard.testing.junit5.ResourceExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(DropwizardExtensionsSupport.class)
class AccountResourceTest {
    private static final AccountDAO ACCOUNT_DAO = mock(AccountDAO.class);
    public static final ResourceExtension RESOURCES = ResourceExtension.builder()
        .addResource(new AccountResource(ACCOUNT_DAO))
        .build();
    private ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
    private Account account;

    @BeforeEach
    public void setUp() {
        account = new Account(BigDecimal.valueOf(5000));
    }

    @AfterEach
    public void tearDown() {
        reset(ACCOUNT_DAO);
    }

    @Test
    public void createAccount() throws JsonProcessingException {
        when(ACCOUNT_DAO.save(any(Account.class))).thenReturn(account);
        final Response response = RESOURCES.target("/account")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(account, MediaType.APPLICATION_JSON_TYPE));

        assertThat(response.getStatusInfo()).isEqualTo(Response.Status.OK);
        verify(ACCOUNT_DAO).save(accountCaptor.capture());
        assertThat(accountCaptor.getValue()).isEqualTo(account);
    }

    @Test
    public void listAccounts() throws Exception {
        final List<Account> accounts = Collections.singletonList(account);
        when(ACCOUNT_DAO.findAll()).thenReturn(accounts);

        final List<Account> response = RESOURCES.target("/account")
            .request().get(new GenericType<List<Account>>() {
        });

        verify(ACCOUNT_DAO).findAll();
        assertThat(response).containsAll(accounts);
    }
}