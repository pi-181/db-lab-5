package com.demkom58.db_lab_4.request.paramless;

import com.demkom58.db_lab_4.DatabaseManager;
import com.demkom58.db_lab_4.util.SwingUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class RequestTableAdapter implements TableAdapter {
    private final DatabaseManager databaseManager;
    private final JTable resultTable;

    public RequestTableAdapter(@NotNull DatabaseManager databaseManager, @NotNull JTable resultTable) {
        this.databaseManager = databaseManager;
        this.resultTable = resultTable;
    }

    public Optional<String> execute(@NotNull String sql) throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             Statement statement = connection.createStatement()) {
            final String lowerCase = sql.toLowerCase();
            if (lowerCase.startsWith("select")) {
                resultTable.setModel(SwingUtil.buildTableModel(statement.executeQuery(sql)));
                return Optional.empty();
            } else if (lowerCase.startsWith("insert") || lowerCase.startsWith("update") || lowerCase.startsWith("delete")) {
                final int count = statement.executeUpdate(sql);
                return Optional.of("Update " + count + " items");
            } else {
                statement.execute(sql);
                return Optional.of("SQL successfully executed!");
            }
        }
    }

}
