package com.backend.tracker.background;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class DatabaseCreation {

    private static final Logger logger = LogManager.getLogger(DatabaseCreation.class);

    private DatabaseCreation() {

    }

    public static void checkTrackerDatabase() {
        DriverManagerDataSource expenseDB = new DriverManagerDataSource();
        expenseDB.setDriverClassName("org.postgresql.Driver");
        expenseDB.setUrl("jdbc:postgresql://127.0.0.1:5432/postgres");
        expenseDB.setUsername("postgres");
        expenseDB.setPassword("");

        JdbcTemplate jdbcConnection = new JdbcTemplate(expenseDB);
        Integer count = jdbcConnection
                .queryForObject("SELECT count(*) as cnt FROM pg_database WHERE datname = 'expenses'", Integer.class);
        if (count == null || count == 0) {
            jdbcConnection.update("CREATE DATABASE expenses");
        }
        // createExpensesSchema();
    }

    // public static void createExpensesSchema() {
    //     DriverManagerDataSource dbTelemetry = new DriverManagerDataSource();
    //     dbTelemetry.setDriverClassName("org.postgresql.Driver");
    //     dbTelemetry.setUrl("jdbc:postgresql://127.0.0.1:5432/telemetry");
    //     dbTelemetry.setUsername("postgres");
    //     dbTelemetry.setPassword("");
    //     JdbcTemplate jdbcConnection = new JdbcTemplate(dbTelemetry);

    //     Integer count = jdbcConnection
    //             .queryForObject("SELECT count(*) FROM information_schema.schemata WHERE schema_name = 'coralnms'",
    //                     Integer.class);
    //     try {
    //         jdbcConnection.update("create EXTENSION IF NOT EXISTS \"postgis\"");
    //     } catch (Exception e) {
    //         logger.error("PostGIS extension could not be created. Please check your PostGIS installation.", e);
    //     }
    //     try {
    //         jdbcConnection.update("create EXTENSION IF NOT EXISTS \"pgcrypto\"");
    //     } catch (Exception e) {
    //         logger.error("pgcrypto extension could not be created. Please check your PostgreSQL installation.", e);
    //     }

    //     if (count == null || count == 0) {
    //         jdbcConnection.update("CREATE SCHEMA coralnms");
    //     }
    // }

    
}
