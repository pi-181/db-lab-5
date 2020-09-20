package com.demkom58.jaslab2.table;

import com.demkom58.jaslab2.DatabaseManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.Optional;
import java.util.Vector;

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
                resultTable.setModel(buildTableModel(statement.executeQuery(sql)));
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

    private DefaultTableModel buildTableModel(ResultSet rs) throws SQLException {
        final ResultSetMetaData metaData = rs.getMetaData();

        // names of columns
        final Vector<String> columnNames = new Vector<>();
        final int columnCount = metaData.getColumnCount();
        for (int column = 1; column <= columnCount; column++)
            columnNames.add(metaData.getColumnName(column));

        // data of the table
        final Vector<Vector<Object>> data = new Vector<>();
        while (rs.next()) {
            final Vector<Object> vector = new Vector<>();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                Object object = rs.getObject(columnIndex);
                if (rs.wasNull())
                    object = "<null>";
                vector.add(object);
            }
            data.add(vector);
        }

        return new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

    }

}
