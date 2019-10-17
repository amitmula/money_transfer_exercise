package com.revolut.exerice;

import com.revolut.exerice.core.Account;
import com.revolut.exerice.db.AccountDAO;
import com.revolut.exerice.resources.AccountResource;
import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class MoneyTransferApplication extends Application<MoneyTransferConfiguration> {

    public static void main(final String[] args) throws Exception {
        new MoneyTransferApplication().run(args);
    }

    private final HibernateBundle<MoneyTransferConfiguration> hibernateBundle =
        new HibernateBundle<MoneyTransferConfiguration>(Account.class) {
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
        final AccountDAO dao = new AccountDAO(hibernateBundle.getSessionFactory());
        environment.jersey().register(new AccountResource(dao));
    }
}
