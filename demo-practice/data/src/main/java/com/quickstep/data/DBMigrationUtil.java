package com.quickstep.data;

import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.callback.Callback;
import org.flywaydb.core.api.callback.Context;
import org.flywaydb.core.api.callback.Event;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Properties;


public class DBMigrationUtil {

    private static final Logger logger = LoggerFactory.getLogger(DBMigrationUtil.class);

    private DataProperties dataProperties;


    public void initOrMigrateAndOptionallyRunJooq(boolean generateJooqCode, List<String> profiles) {
        this.dataProperties = DataProperties.readFromPropertiesFile(profiles);
        switch (dataProperties.migrationType) {
            case NukeAndPave -> {
                dropSchema();
                doMigration(generateJooqCode, null);
                break;
            }
            case Scripts -> {
                doMigration(generateJooqCode, null);
                break;
            }
            default -> {
                doNone();
                break;
            }
        }
    }

    public void generateJooqCodeFromEmbeddedPostgres() {
        try {
            final var start = Instant.now();
            logger.info("Starting JOOQ Code Generation via Embedded Postgresql (https://github.com/zonkyio/embedded-postgres)...");

            //Start an embedded postgres server
            final var embeddedPostgres = EmbeddedPostgres.start();

            //Post flyway at that embedded database
            doMigration(false, embeddedPostgres.getPostgresDatabase());

            //Generate jooq code from embedded database
            JOOQGenerator.generateJOOQSource(embeddedPostgres.getPostgresDatabase());

            //Log how long it took
            logger.info("JOOQ Code generation took " + Duration.between(start, Instant.now()).toMillis() + " milliseconds");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void doMigration(boolean generateJooqCode, DataSource dataSource) {
        final FluentConfiguration flywayConf = Flyway.configure();

        if (dataSource != null) {
            flywayConf.dataSource(dataSource);
        } else {
            flywayConf.dataSource(dataProperties.url, dataProperties.user, dataProperties.password)
                    .defaultSchema(dataProperties.schema)
                    .schemas(dataProperties.schema);
        }

        if (generateJooqCode) {
            flywayConf.callbacks(new MigrationCallback(dataProperties));
        }

        flywayConf.load().migrate();
    }

    private void dropSchema() {
        final var props = new Properties();
        props.setProperty("user", dataProperties.user);
        props.setProperty("password", dataProperties.password);
        props.setProperty("sslmode", dataProperties.sslMode);
        final var schema = dataProperties.schema;

        try (Connection conn = DriverManager.getConnection(dataProperties.url + "?currentSchema=" + schema, props)) {
            // DROP SCHEMA
            try (Statement statement = conn.createStatement()) {
                statement.executeUpdate("DROP SCHEMA IF EXISTS " + schema + " CASCADE");
            } catch (SQLException e) {
                throw new RuntimeException("Cannot drop schema", e);
            }

        } catch (Throwable e) {
            throw new RuntimeException("Error doing drop/create schema", e);
        }
    }


    private void doNone() {
        logger.info("Database is in 'Do None' migration mode.");
    }

    private static class MigrationCallback implements Callback {
        DataProperties dataProperties;

        public MigrationCallback(DataProperties dataProperties) {
            this.dataProperties = dataProperties;
        }

        @Override
        public boolean supports(Event event, Context context) {
            return event.equals(Event.AFTER_MIGRATE_APPLIED) || event.equals(Event.AFTER_MIGRATE);
        }

        @Override
        public boolean canHandleInTransaction(Event event, Context context) {
            return true;
        }

        // generate new JOOQ source files only when a new migration has been applied
        @Override
        public void handle(Event event, Context context) {
            if (event.equals(Event.AFTER_MIGRATE_APPLIED)) {
                JOOQGenerator.generateJOOQSource(dataProperties);
            }
        }

        @Override
        public String getCallbackName() {
            return DBMigrationUtil.class.getSimpleName();
        }
    }
}
