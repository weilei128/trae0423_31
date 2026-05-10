package com.pharmacy.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@Component
public class DatabaseInitRunner implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitRunner.class);

    private final DataSource dataSource;

    @Value("classpath:schema.sql")
    private Resource schemaSql;

    @Value("classpath:data.sql")
    private Resource dataSql;

    private static final String[] TABLE_NAMES = {
            "expiry_alert",
            "inventory_item",
            "inventory_task",
            "stock",
            "stock_out_item",
            "stock_out",
            "stock_in_item",
            "stock_in",
            "purchase_plan_item",
            "purchase_plan",
            "warehouse_location",
            "department",
            "supplier",
            "drug_dict",
            "drug_category"
    };

    public DatabaseInitRunner(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.info("Initializing database schema...");
        try {
            dropExistingTables();
            ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
            populator.addScript(schemaSql);
            populator.setContinueOnError(true);
            populator.execute(dataSource);
            logger.info("Database schema initialized successfully");
            
            logger.info("Initializing test data...");
            ResourceDatabasePopulator dataPopulator = new ResourceDatabasePopulator();
            dataPopulator.addScript(dataSql);
            dataPopulator.setContinueOnError(true);
            dataPopulator.execute(dataSource);
            logger.info("Test data initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize database", e);
        }
    }

    private void dropExistingTables() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("SET FOREIGN_KEY_CHECKS = 0");
            for (String tableName : TABLE_NAMES) {
                try {
                    stmt.execute("DROP TABLE IF EXISTS " + tableName);
                    logger.info("Dropped table: {}", tableName);
                } catch (Exception e) {
                    logger.warn("Failed to drop table: {}, error: {}", tableName, e.getMessage());
                }
            }
            stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
            logger.info("All existing tables dropped");
        } catch (Exception e) {
            logger.error("Failed to drop existing tables", e);
        }
    }
}
