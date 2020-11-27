package com.demkom58.db_lab_4.request.parametred;

import com.demkom58.db_lab_4.DatabaseManager;
import com.demkom58.db_lab_4.request.paramless.RequestTableAdapter;
import com.demkom58.db_lab_4.request.paramless.TableAdapter;
import com.demkom58.db_lab_4.util.SwingUtil;
import org.intellij.lang.annotations.Language;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.sql.PreparedStatement;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

public class ParametredController {
    private final JTabbedPane tabbedPane;
    private final JPanel requestPanel;

    private final DatabaseManager databaseManager;
    private final ExecutorService service;

    private final JTextField fromField;
    private final JTextField toField;
    private final JButton goButton;
    private final JTable resultTable;
    private final JLabel statusBar;

    private final TableAdapter tableAdapter;

    public ParametredController(JTabbedPane tabbedPane, JPanel requestPanel, DatabaseManager databaseManager, ExecutorService service,
                                JTextField fromField, JTextField toField, JButton goButton, JTable resultTable, JLabel statusBar) {
        this.tabbedPane = tabbedPane;
        this.requestPanel = requestPanel;

        this.databaseManager = databaseManager;
        this.service = service;

        this.fromField = fromField;
        this.toField = toField;
        this.goButton = goButton;
        this.resultTable = resultTable;
        this.statusBar = statusBar;

        this.tableAdapter = new RequestTableAdapter(databaseManager, this.resultTable);
        goButton.addActionListener(this::handleGoButton);
    }

    private void handleGoButton(ActionEvent event) {
        final Optional<String> from = Optional.of(fromField.getText().trim()).map(t -> t.isEmpty() ? null : t);
        final Optional<String> to = Optional.of(toField.getText().trim()).map(t -> t.isEmpty() ? null : t);

        resultTable.removeAll();
        goButton.setEnabled(false);
        statusBar.setText("Executing...");
        service.execute(() -> {
            try {
                PreparedStatement preparedStatement = databaseManager.getConnection()
                        .prepareStatement(
                                "SELECT * FROM social.remittance WHERE from_user = ? AND to_user = ?"
                        );

                from.ifPresent(s -> sneaky(() -> preparedStatement.setInt(1, Integer.parseInt(s))));
                to.ifPresent(s -> sneaky(() -> preparedStatement.setInt(2, Integer.parseInt(s))));

                resultTable.setModel(SwingUtil.buildTableModel(preparedStatement.executeQuery()));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.getLocalizedMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            } finally {
                goButton.setEnabled(true);
                statusBar.setText("Idling...");
            }
        });
    }

    interface SneakyRunnable {
        void run() throws Throwable;
    }

    public static void sneaky(SneakyRunnable e) throws RuntimeException {
        try {
            e.run();
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }
}
