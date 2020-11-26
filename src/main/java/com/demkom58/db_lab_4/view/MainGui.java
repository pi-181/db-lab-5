package com.demkom58.db_lab_4.view;

import com.demkom58.db_lab_4.DatabaseManager;
import com.demkom58.db_lab_4.table.RequestTableAdapter;
import com.demkom58.db_lab_4.table.TableAdapter;
import org.intellij.lang.annotations.Language;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
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

    private final TableAdapter tableAdapter;

    public MainGui() {
        setContentPane(rootPanel);

        final Dimension minSize = new Dimension(450, 400);
        setMaximumSize(minSize);
        setSize(minSize);

        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setTitle("Laboratory Work №4");

        try {
            databaseManager.setup();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getLocalizedMessage(), "Error occurred while setup", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

        tableAdapter = new RequestTableAdapter(databaseManager, resultTable);

        Runtime.getRuntime().addShutdownHook(new Thread(databaseManager::shutdown));
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
                        JOptionPane.showMessageDialog(this, message,
                        "Info", JOptionPane.INFORMATION_MESSAGE));
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

}
