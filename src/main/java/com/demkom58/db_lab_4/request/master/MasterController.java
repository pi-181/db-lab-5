package com.demkom58.db_lab_4.request.master;

import com.demkom58.db_lab_4.DatabaseManager;
import com.demkom58.db_lab_4.util.*;
import com.google.common.collect.*;
import org.intellij.lang.annotations.Language;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

public class MasterController {
    @Language("SQL")
    private static final String SELECT_TABLES = "SELECT table_name FROM information_schema.tables WHERE table_schema=?";
    @Language("SQL")
    private static final String SELECT_ALL_FROM = "SELECT * FROM ";
    @Language("SQL")
    private static final String SELECT_FOREIGN_KEYS = "SELECT" +
            "    kcu.table_name," +
            "    kcu.column_name," +
            "    ccu.column_name AS foreign_column_name," +
            "    ccu.table_name AS foreign_table_name" +
            " FROM" +
            "    information_schema.table_constraints AS tc" +
            "    JOIN information_schema.key_column_usage AS kcu" +
            "      ON tc.constraint_name = kcu.constraint_name" +
            "      AND tc.table_schema = kcu.table_schema" +
            "    JOIN information_schema.constraint_column_usage AS ccu" +
            "      ON ccu.constraint_name = tc.constraint_name" +
            " WHERE tc.constraint_type = 'FOREIGN KEY'";


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
    private JComboBox<NamedObject<Column>> slavesVariantsBox;

    private String selectedMasterTable;
    private Multimap<Column, Column> foreignKeysMasterTable = HashMultimap.create();
    private Multimap<Column, Column> foreignKeysMasterTableInverted = HashMultimap.create();
    private Object selectedMasterValue;

    private String selectedSlaveTable;

    public MasterController(JTabbedPane tabbedPane, JPanel requestPanel, DatabaseManager databaseManager, ExecutorService service,
                            JTable masterTable, JTable slaveTable, JTextField schemaNameField, JComboBox<String> variantsBox,
                            JButton goButton, JLabel statusBar, JComboBox<NamedObject<Column>> slavesVariantsBox) {
        this.tabbedPane = tabbedPane;
        this.requestPanel = requestPanel;

        this.databaseManager = databaseManager;
        this.service = service;

        this.masterTable = masterTable;
        this.slaveTable = slaveTable;

        this.masterSchemaNameField = schemaNameField;
        this.variantsBox = variantsBox;
        this.goButton = goButton;
        this.statusBar = statusBar;

        this.slavesVariantsBox = slavesVariantsBox;

        goButton.addActionListener(e -> update());
        this.variantsBox.addItemListener(e -> {
            if (e.getStateChange() != ItemEvent.SELECTED)
                return;
            selectMasterTable(e.getItem().toString());
        });
        this.slavesVariantsBox.addItemListener(e -> {
            if (e.getStateChange() != ItemEvent.SELECTED)
                return;
            selectSlaveTable(((NamedObject<Column>) e.getItem()).getObject());
        });
        masterTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                JTable t = masterTable;
                int row = t.rowAtPoint(evt.getPoint());
                int col = t.columnAtPoint(evt.getPoint());
                if (row >= 0 && col >= 0) {
                    TableModel model = t.getModel();
                    Object valueAt = model.getValueAt(row, col);
                    String columnName = model.getColumnName(col);
                    selectMasterColumn(selectedMasterTable, columnName, valueAt);
                }
            }
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

                Pair<Multimap<Column, Column>, Multimap<Column, Column>> keys = mapForeignKeys(c);
                foreignKeysMasterTable = keys.getA();
                foreignKeysMasterTableInverted = keys.getB();
            }
        });
    }

    public void selectMasterTable(String table) {
        slavesVariantsBox.setModel(new DefaultComboBoxModel<>());
        slaveTable.setModel(new DefaultTableModel());
        selectedSlaveTable = null;
        selectedMasterValue = null;

        selectedMasterTable = table;
        if (table == null) {
            masterTable.setModel(new DefaultTableModel());
            foreignKeysMasterTable = null;
            return;
        }

        process(() -> {
            try (final Connection c = databaseManager.getConnection()) {
                final ResultSet resultSet = c.createStatement()
                        .executeQuery(SELECT_ALL_FROM + formatTable(table));
                masterTable.setModel(SwingUtil.buildTableModel(resultSet));
            }
        });
    }

    public void selectMasterColumn(String table, String column, Object is) {
        selectedMasterValue = is;
        selectedSlaveTable = table;
        slavesVariantsBox.setModel(new DefaultComboBoxModel<>());
        if (table == null) {
            slaveTable.setModel(new DefaultTableModel());
            return;
        }

        Column fromColumn = new Column(selectedMasterTable, column);
        Collection<Column> to = foreignKeysMasterTable.get(fromColumn); // allow select
        Collection<Column> from = foreignKeysMasterTableInverted.get(fromColumn);

        List<NamedObject<Column>> all =
                new ArrayList<>(to.stream().map(a -> new NamedObject<>(fromColumn + " -> " + a, a)).collect(Collectors.toList()));
        all.addAll(from.stream().map(a -> new NamedObject<>(fromColumn + " <- " + a, a)).collect(Collectors.toList()));
        if (all.isEmpty()) {
            selectSlaveTable(null);
            slavesVariantsBox.setModel(new DefaultComboBoxModel<>());
            return;
        }

        slavesVariantsBox.setModel(new DefaultComboBoxModel<>(all.toArray(new NamedObject[0])));

        final NamedObject<Column> target = all.stream().findFirst().get();
        selectSlaveTable(target.getObject());
    }

    private void selectSlaveTable(Column target) {
        if (target == null) {
            slaveTable.setModel(new DefaultTableModel());
            return;
        }

        process(() -> {
            try (final Connection c = databaseManager.getConnection()) {
                final PreparedStatement preparedStatement = c.prepareStatement(
                        "SELECT * FROM " + formatTable(target.getTableName()) + " WHERE " + target.getColumnName() + " = ?"
                );

                preparedStatement.setObject(1, selectedMasterValue);
                slaveTable.setModel(SwingUtil.buildTableModel(preparedStatement.executeQuery()));
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

    private Pair<Multimap<Column, Column>, Multimap<Column, Column>> mapForeignKeys(Connection c) throws Exception {
        final Multimap<Column, Column> keys = HashMultimap.create();
        final Multimap<Column, Column> keysInverted = HashMultimap.create();

        final Statement preparedStatement = c.createStatement();
        final ResultSet resultSet = preparedStatement.executeQuery(SELECT_FOREIGN_KEYS);
        while (resultSet.next()) {
            String sourceTable = resultSet.getString("table_name");
            String sourceColumn = resultSet.getString("column_name");
            String targetTable = resultSet.getString("foreign_table_name");
            String targetColumn = resultSet.getString("foreign_column_name");

            Column from = new Column(sourceTable, sourceColumn);
            Column to = new Column(targetTable, targetColumn);

            keys.put(from, to);
            keysInverted.put(to, from);
        }

        return new Pair<>(keys, keysInverted);
    }

    private String formatTable(String name) {
        return masterSchemaNameField.getText() + "." + name;
    }
}
