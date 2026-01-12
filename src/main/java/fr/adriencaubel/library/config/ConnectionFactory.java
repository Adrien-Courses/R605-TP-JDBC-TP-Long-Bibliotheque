package fr.adriencaubel.library.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConnectionFactory {
    private ConnectionFactory() {}

    public static Connection openConnection() throws SQLException {
        return DriverManager.getConnection(DbConfig.jdbcUrl(), DbConfig.user(), DbConfig.password());
    }
}
