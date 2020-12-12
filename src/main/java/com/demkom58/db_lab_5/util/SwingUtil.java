package com.demkom58.db_lab_5.util;

import org.jetbrains.annotations.NotNull;

import javax.swing.table.DefaultTableModel;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;

public final class SwingUtil {

    @NotNull
    public static DefaultTableModel buildTableModel(@NotNull final ResultSet rs) throws SQLException {
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
