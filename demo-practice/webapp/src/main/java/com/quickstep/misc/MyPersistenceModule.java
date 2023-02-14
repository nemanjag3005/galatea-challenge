package com.quickstep.misc;

import com.google.common.util.concurrent.MoreExecutors;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import com.quickstep.jooqpersist.JooqPersistModule;
import com.quickstep.misc.guice.server.VaadinModule;
import com.quickstep.ui.util.GlobalEventBus;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jooq.Configuration;
import org.jooq.SQLDialect;
import org.jooq.conf.MappedSchema;
import org.jooq.conf.RenderMapping;
import org.jooq.impl.DefaultConfiguration;

import javax.sql.DataSource;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class MyPersistenceModule extends AbstractModule {

    private static Properties properties;

    private String schema;
    private String jdbcUrlWithSchemaDefault;
    private String dbUser;
    private String dbPass;

    private int hikariConnectionTimeout;
    private int hikariMinimumIdle;
    private int hikariMaximumPoolSize;
    private int hikariIdleTimeout;

    private String uploaderDownloaderType;


    public MyPersistenceModule() {
        this.properties = VaadinModule.getProperties();
        readPropertiesToFields();
    }

    @Override
    protected void configure() {
        Names.bindProperties(binder(), properties);
        //Executor service show
        final ExecutorService executor = MoreExecutors.getExitingExecutorService((ThreadPoolExecutor) Executors.newFixedThreadPool(10));
        bind(ExecutorService.class).toInstance(executor);

        final ExecutorService globalEventExecutor = MoreExecutors.getExitingExecutorService((ThreadPoolExecutor) Executors.newFixedThreadPool(100));
        // Global EventBus
        bind(GlobalEventBus.class).toInstance(new GlobalEventBus(globalEventExecutor));

        //Hikary DataSource & DSL Context
        {
            install(new JooqPersistModule());
            HikariConfig config = new HikariConfig();
            HikariDataSource ds;
            config.setJdbcUrl(jdbcUrlWithSchemaDefault);
            config.setUsername(dbUser);
            config.setPassword(dbPass);
            config.setSchema(schema);
            config.setConnectionTimeout(hikariConnectionTimeout);
            config.setMinimumIdle(hikariMinimumIdle);
            config.setIdleTimeout(hikariIdleTimeout);
            config.setMaximumPoolSize(hikariMaximumPoolSize);
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

            ds = new HikariDataSource(config);
            bind(DataSource.class).toInstance(ds);
        }
    }

    @Provides
    public SQLDialect dialect() {
        return SQLDialect.POSTGRES;
    }

    @Provides
    public Configuration configuration(DataSource dataSource, SQLDialect dialect) {
        var conf = new DefaultConfiguration()
                .set(dataSource)
                .set(dialect);
        conf.settings().withRenderMapping(new RenderMapping()
                .withSchemata(
                        new MappedSchema().withInput("public")
                                .withOutput(schema)));
        return conf;
    }

    private void readPropertiesToFields() {
        //Set schema name
        this.schema = properties.getProperty("demo.config.db.schema");
        final var jdbcUrl = properties.getProperty("demo.config.db.url");
        this.jdbcUrlWithSchemaDefault = jdbcUrl + "?currentSchema=" + schema;
        this.dbUser = properties.getProperty("demo.config.db.user");
        this.dbPass = properties.getProperty("demo.config.db.pass");

        //Hikari CP Settings
        this.hikariConnectionTimeout = Integer.parseInt(properties.getProperty("hibernate.hikari.connectionTimeout"));
        this.hikariMinimumIdle = Integer.parseInt(properties.getProperty("hibernate.hikari.minimumIdle"));
        this.hikariMaximumPoolSize = Integer.parseInt(properties.getProperty("hibernate.hikari.maximumPoolSize"));
        this.hikariIdleTimeout = Integer.parseInt(properties.getProperty("hibernate.hikari.idleTimeout"));

        this.uploaderDownloaderType = properties.getProperty("demo.config.downloadupload.environment");
    }
}
