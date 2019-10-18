package com.revolut.exerice;

import com.revolut.exerice.core.Account;
import com.revolut.exerice.core.TransferRequest;
import com.revolut.exerice.core.TransferStatus;
import io.dropwizard.testing.ConfigOverride;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(DropwizardExtensionsSupport.class)
public class IntegrationTest {
    private static final String TMP_FILE = createTempFile();
    private static final String CONFIG_PATH = ResourceHelpers.resourceFilePath("test-config.yml");

    public static final DropwizardAppExtension<MoneyTransferConfiguration> RULE = new DropwizardAppExtension<>(
            MoneyTransferApplication.class, CONFIG_PATH,
            ConfigOverride.config("database.url", "jdbc:h2:" + TMP_FILE));

    @BeforeAll
    public static void migrateDb() throws Exception {
        RULE.getApplication().run("db", "migrate", CONFIG_PATH);
    }

    private static String createTempFile() {
        try {
            return File.createTempFile("test-example", null).getAbsolutePath();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Test
    public void testAddAccount() {
        final Account account = new Account(BigDecimal.valueOf(5000));
        final Account newAccount = postAccount(account);
        assertThat(newAccount.getId()).isNotNull();
        assertThat(newAccount.getBalance()).isEqualTo(BigDecimal.valueOf(5000));
    }

    @Test
    public void testMakeTransfer() {
        final Account senderAccount = new Account(BigDecimal.valueOf(5000));
        final Account receiverAccount = new Account(BigDecimal.valueOf(5000));
        Account newSenderAccount = postAccount(senderAccount);
        Account newRecipientAccount = postAccount(receiverAccount);
        final TransferRequest transferRequest = new TransferRequest();
        transferRequest.setSenderAccountId(newSenderAccount.getId());
        transferRequest.setRecipientAccountId(newRecipientAccount.getId());
        transferRequest.setAmount(BigDecimal.valueOf(500));
        Map transferRequestResponse = postTransferRequest(transferRequest);
        String transferRequestId = transferRequestResponse.get("id").toString();
        assertThat(transferRequestId != null);
        while(getTransferRequest(Long.valueOf(transferRequestId)).getStatus() != TransferStatus.PROCESSED);
        assertThat(getAccount(newSenderAccount.getId()).getBalance().compareTo(BigDecimal.valueOf(4500)) == 0);
        assertThat(getAccount(newRecipientAccount.getId()).getBalance().compareTo(BigDecimal.valueOf(5500)) == 0);
    }

    private Account postAccount(Account account) {
        return RULE.client().target("http://localhost:" + RULE.getLocalPort() + "/account")
            .request()
            .post(Entity.entity(account, MediaType.APPLICATION_JSON_TYPE))
            .readEntity(Account.class);
    }

    private Map postTransferRequest(TransferRequest transferRequest) {
        return RULE.client().target("http://localhost:" + RULE.getLocalPort() + "/transfer")
            .request()
            .post(Entity.entity(transferRequest, MediaType.APPLICATION_JSON_TYPE))
            .readEntity(Map.class);
    }

    private TransferRequest getTransferRequest(long id) {
        return RULE.client().target("http://localhost:" + RULE.getLocalPort() + "/transfer/" + id)
            .request()
            .get(TransferRequest.class);
    }

    private Account getAccount(long id) {
        return RULE.client().target("http://localhost:" + RULE.getLocalPort() + "/account/" + id)
            .request()
            .get(Account.class);
    }

}
