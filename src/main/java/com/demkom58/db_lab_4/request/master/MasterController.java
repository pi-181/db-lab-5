package com.demkom58.db_lab_4.request.master;

import com.demkom58.db_lab_4.DatabaseManager;
import com.demkom58.db_lab_4.util.SwingUtil;
import com.demkom58.db_lab_4.util.ThrowableRunnable;
import org.intellij.lang.annotations.Language;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class MasterController {
    @Language("SQL")
    private static final String SELECT_TABLES = "SELECT table_name FROM information_schema.tables WHERE table_schema=?";
    @Language("SQL")
    private static final String SELECT_ALL_FROM = "SELECT * FROM ";

    private final JTabbedPane tabbedPane;
    private final JPanel requestPanel;

    private final DatabaseManager databaseManager;
    private final ExecutorService service;

    private JTable masterTable;
    private JTable slaveTable;
    private JTextField masterSchemaNameField;
    private JComboBox<String> variantsBox;
    private JButton goButton;
    private JLabel statusBar;

    private String selectedMasterTable;
    private String selectedSlaveTable;

    public MasterController(JTabbedPane tabbedPane, JPanel requestPanel, DatabaseManager databaseManager, ExecutorService service,
                            JTable masterMasterTable, JTable masterSlaveTable, JTextField masterSchemaNameField,
                            JComboBox<String> masterVariantsBox, JButton masterGoButton, JLabel statusBar) {
        this.tabbedPane = tabbedPane;
        this.requestPanel = requestPanel;

        this.databaseManager = databaseManager;
        this.service = service;

        this.masterTable = masterMasterTable;
        this.slaveTable = masterSlaveTable;

        this.masterSchemaNameField = masterSchemaNameField;
        this.variantsBox = masterVariantsBox;
        this.goButton = masterGoButton;
        this.statusBar = statusBar;

        masterGoButton.addActionListener(e -> update());
        variantsBox.addItemListener(e -> {
            if (e.getStateChange() != ItemEvent.SELECTED)
                return;
            selectMasterTable(e.getItem().toString());
        });
    }

    public void update() {
        selectMasterTable(null);
        process(() -> {
            final List<String> tableNames = new ArrayList<>();
            try (final Connection c = databaseManager.getConnection()) {
                final PreparedStatement preparedStatement = c.prepareStatement(SELECT_TABLES);
                preparedStatement.setString(1, masterSchemaNameField.getText());

                final ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next())
                    tableNames.add(resultSet.getString("table_name"));

                variantsBox.setModel(new DefaultComboBoxModel<>(tableNames.toArray(new String[0])));
                if (tableNames.size() > 0)
                    selectMasterTable(tableNames.get(0));
            }
        });
    }

    public void selectMasterTable(String table) {
        slaveTable.removeAll();
        selectedSlaveTable = null;

        selectedMasterTable = table;
        if (table == null) {
            masterTable.removeAll();
            return;
        }

        process(() -> {
            try (final Connection c = databaseManager.getConnection()) {
                masterTable.setModel(SwingUtil.buildTableModel(c.createStatement()
                        .executeQuery(SELECT_ALL_FROM + masterSchemaNameField.getText() + "." + table)
                ));
            }
        });
    }

    private void process(ThrowableRunnable command) {
        goButton.setEnabled(false);
        statusBar.setText("Refreshing...");
        service.execute(() -> {
            try {
                command.run();
            } catch (Throwable e) {
                JOptionPane.showMessageDialog(null, e.getLocalizedMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            } finally {
                goButton.setEnabled(true);
                statusBar.setText("Idling...");
            }
        });
    }
}
