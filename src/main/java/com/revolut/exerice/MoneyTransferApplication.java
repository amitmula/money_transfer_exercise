package com.revolut.exerice;

import com.revolut.exerice.core.Account;
import com.revolut.exerice.core.TransferRequest;
import com.revolut.exerice.db.AccountDAO;
import com.revolut.exerice.db.TransferRequestDAO;
import com.revolut.exerice.resources.AccountResource;
import com.revolut.exerice.resources.MoneyTransferResource;
import com.revolut.exerice.util.MoneyTransferProcessor;
import com.revolut.exerice.util.MoneyTransferQueue;
import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class MoneyTransferApplication extends Application<MoneyTransferConfiguration> {

    protected static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static void main(final String[] args) throws Exception {
        new MoneyTransferApplication().run(args);
    }

    private final HibernateBundle<MoneyTransferConfiguration> hibernateBundle =
            new HibernateBundle<MoneyTransferConfiguration>(Account.class, TransferRequest.class) {
                @Override
                public DataSourceFactory getDataSourceFactory(MoneyTransferConfiguration configuration) {
                    return configuration.getDataSourceFactory();
                }
            };

    @Override
    public String getName() {
        return "MoneyTransfer";
    }

    @Override
    public void initialize(final Bootstrap<MoneyTransferConfiguration> bootstrap) {
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(
                        bootstrap.getConfigurationSourceProvider(),
                        new EnvironmentVariableSubstitutor(false)
                )
        );
        bootstrap.addBundle(new MigrationsBundle<MoneyTransferConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(MoneyTransferConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }
        });
        bootstrap.addBundle(hibernateBundle);
    }

    @Override
    public void run(final MoneyTransferConfiguration configuration,
                    final Environment environment) {
        BlockingQueue<TransferRequest> transferRequestQueue = new ArrayBlockingQueue<>(1000);
        AccountDAO accountDAO = new AccountDAO(hibernateBundle.getSessionFactory());
        TransferRequestDAO transferRequestDAO = new TransferRequestDAO(hibernateBundle.getSessionFactory());
        MoneyTransferQueue moneyTransferQueue = new MoneyTransferQueue(transferRequestQueue);
        MoneyTransferProcessor proxyTransferProcessor = new UnitOfWorkAwareProxyFactory(hibernateBundle)
            .create(MoneyTransferProcessor.class,
                new Class[]{SessionFactory.class, BlockingQueue.class, AccountDAO.class, TransferRequestDAO.class},
                new Object[]{hibernateBundle.getSessionFactory(), transferRequestQueue, accountDAO, transferRequestDAO}
            );
        new Thread(proxyTransferProcessor).start();
        environment.jersey().register(new AccountResource(accountDAO));
        environment.jersey().register(new MoneyTransferResource(transferRequestDAO, moneyTransferQueue));
    }
}
