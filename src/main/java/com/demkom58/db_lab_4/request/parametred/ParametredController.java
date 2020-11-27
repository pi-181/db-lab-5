package com.demkom58.db_lab_4.request.parametred;

import com.demkom58.db_lab_4.DatabaseManager;

import javax.swing.*;
import java.util.concurrent.ExecutorService;

public class ParametredController {
    private final JTabbedPane tabbedPane;
    private final JPanel requestPanel;

    private final DatabaseManager databaseManager;
    private final ExecutorService service;

    private final JTextField parametredFromField;
    private final JTextField parametredToField;
    private final JButton parametredGoButton;
    private final JTable parametredResultTable;
    private final JLabel parametredStatusBar;

    public ParametredController(JTabbedPane tabbedPane, JPanel requestPanel, DatabaseManager databaseManager, ExecutorService service,
                                JTextField parametredFromField, JTextField parametredToField, JButton parametredGoButton,
                                JTable parametredResultTable, JLabel parametredStatusBar) {
        this.tabbedPane = tabbedPane;
        this.requestPanel = requestPanel;

        this.databaseManager = databaseManager;
        this.service = service;

        this.parametredFromField = parametredFromField;
        this.parametredToField = parametredToField;
        this.parametredGoButton = parametredGoButton;
        this.parametredResultTable = parametredResultTable;
        this.parametredStatusBar = parametredStatusBar;
    }
}
