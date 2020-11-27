package com.demkom58.db_lab_4.request.parametred;

import com.demkom58.db_lab_4.DatabaseManager;

import javax.swing.*;
import java.util.concurrent.ExecutorService;

public class ParametredController {
    private final JTabbedPane tabbedPane;
    private final JPanel requestPanel;

    private final DatabaseManager databaseManager;
    private final ExecutorService service;


    public ParametredController(JTabbedPane tabbedPane, JPanel requestPanel, DatabaseManager databaseManager, ExecutorService service) {
        this.tabbedPane = tabbedPane;
        this.requestPanel = requestPanel;

        this.databaseManager = databaseManager;
        this.service = service;
    }
}
