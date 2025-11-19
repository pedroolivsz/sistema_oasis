package com.io.github.pedroolivsz.config;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class Database {

    private static final HikariDataSource dataSource = new HikariDataSource();

    static {
        dataSource.setJdbcUrl(Config.get("db.url"));
        dataSource.setMaximumPoolSize(10);
    }
    public static Connection connect() throws SQLException {
        return dataSource.getConnection();
    }
}
