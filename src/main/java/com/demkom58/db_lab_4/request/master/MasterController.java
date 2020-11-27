package com.demkom58.db_lab_4.request.master;

import com.demkom58.db_lab_4.DatabaseManager;

import javax.swing.*;
import java.util.concurrent.ExecutorService;

public class MasterController {
    private final JTabbedPane tabbedPane;
    private final JPanel requestPanel;

    private final DatabaseManager databaseManager;
    private final ExecutorService service;


    public MasterController(JTabbedPane tabbedPane, JPanel requestPanel, DatabaseManager databaseManager, ExecutorService service) {
        this.tabbedPane = tabbedPane;
        this.requestPanel = requestPanel;

        this.databaseManager = databaseManager;
        this.service = service;
    }
}
