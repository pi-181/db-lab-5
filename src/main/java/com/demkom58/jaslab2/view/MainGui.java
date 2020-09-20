package com.demkom58.jaslab2.view;

import com.demkom58.jaslab2.DatabaseManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainGui extends JFrame {
    private JPanel rootPanel;
    private JTextField sqlField;
    private JButton goButton;
    private JTable resultTable;
    private JLabel statusBar;

    private final DatabaseManager databaseManager = new DatabaseManager();
    private final ExecutorService service = Executors.newFixedThreadPool(1, r -> {
        Thread t = Executors.defaultThreadFactory().newThread(r);
        t.setDaemon(true);
        return t;
    });

    public MainGui() {
        setContentPane(rootPanel);

        final Dimension minSize = new Dimension(450, 400);
        setMaximumSize(minSize);
        setSize(minSize);

        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setTitle("Laboratory Work №2");

        try {
            databaseManager.setup();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getLocalizedMessage(), "Error occurred while setup", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(databaseManager::shutdown));
        goButton.addActionListener(this::handleGoButton);
    }

    private void handleGoButton(ActionEvent event) {
        resultTable.removeAll();
        final String text = sqlField.getText();
        goButton.setEnabled(false);
        statusBar.setText("Executing...");
        service.execute(() -> {
            try (Connection connection = databaseManager.getConnection();
                 Statement statement = connection.createStatement()) {
                final String lowerCase = text.toLowerCase();
                if (lowerCase.startsWith("select")) {
                    resultTable.setModel(buildTableModel(statement.executeQuery(text)));
                } else if (lowerCase.startsWith("insert") || lowerCase.startsWith("update") || lowerCase.startsWith("delete")) {
                    final int count = statement.executeUpdate(text);
                    JOptionPane.showMessageDialog(this, "Update " + count + " items",
                            "Info", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    statement.execute(text);
                    JOptionPane.showMessageDialog(this, "SQL successfully executed!",
                            "Info", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, e.getLocalizedMessage(),
                        "SQL error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            } finally {
                goButton.setEnabled(true);
                statusBar.setText("Idling...");
            }
        });
    }

    public static DefaultTableModel buildTableModel(ResultSet rs) throws SQLException {
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
