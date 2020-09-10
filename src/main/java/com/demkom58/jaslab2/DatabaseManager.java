package com.demkom58.jaslab2;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    private final String host;
    private final String database;
    private final String port;
    private final String user;
    private final String password;

    private Connection connection;

    public DatabaseManager() {
        this.host = System.getenv("host");
        this.database = System.getenv("database");
        this.port = System.getenv("port");
        this.user = System.getenv("user");
        this.password = System.getenv("password");
    }

    public void setup() throws ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
    }

    @NotNull
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(
                    "jdbc:postgresql://" + host + ":" + port + "/" + database, user, password
            );
        }

        return connection;
    }

    public void shutwdown() {
        if (connection == null)
            return;

        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
