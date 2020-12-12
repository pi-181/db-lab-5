package com.demkom58.db_lab_5.request.paramless;

import com.demkom58.db_lab_5.DatabaseManager;
import org.intellij.lang.annotations.Language;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;

public class ParamlessController {
    private final JTabbedPane tabbedPane;
    private final JPanel requestPanel;

    private final DatabaseManager databaseManager;
    private final ExecutorService service;

    private final JTextField sqlField;
    private final JButton goButton;
    private final JTable resultTable;
    private final JLabel statusBar;

    private final TableAdapter tableAdapter;

    public ParamlessController(JTabbedPane tabbedPane, JPanel requestPanel, DatabaseManager databaseManager,
                               ExecutorService service, JTextField sqlField, JButton goButton, JTable resultTable, JLabel statusBar) {
        this.tabbedPane = tabbedPane;
        this.requestPanel = requestPanel;

        this.databaseManager = databaseManager;
        this.service = service;

        this.sqlField = sqlField;
        this.goButton = goButton;
        this.resultTable = resultTable;
        this.statusBar = statusBar;

        this.tableAdapter = new RequestTableAdapter(databaseManager, resultTable);
        goButton.addActionListener(this::handleGoButton);
    }

    private void handleGoButton(ActionEvent event) {
        @Language("SQL")
        final String text = sqlField.getText();

        resultTable.removeAll();
        goButton.setEnabled(false);
        statusBar.setText("Executing...");
        service.execute(() -> {
            try {
                tableAdapter.execute(text).ifPresent(message ->
                        JOptionPane.showMessageDialog(null, message,
                                "Info", JOptionPane.INFORMATION_MESSAGE));
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e.getLocalizedMessage(),
                        "SQL error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            } finally {
                goButton.setEnabled(true);
                statusBar.setText("Idling...");
            }
        });
    }
}
