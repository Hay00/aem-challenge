package com.adobe.aem.challenge.core.service;

import com.day.commons.datasource.poolservice.DataSourcePool;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;

@Component(immediate = true, service = DatabaseService.class)
public class DatabaseServiceImpl implements DatabaseService {
    private final Logger logger = LoggerFactory.getLogger(DatabaseServiceImpl.class);

    @Reference
    private DataSourcePool dataSourcePool;

    @Override
    public Connection getConnection() {
        try {
            DataSource dataSource = (DataSource) dataSourcePool.getDataSource("desafio");
            logger.debug("Connection obtained");
            return dataSource.getConnection();
        } catch (Exception e) {
            logger.debug(String.format("Unable to connect to database. Error msg: %s", e.getMessage()));
            return null;
        }
    }
}
