package com.demkom58.db_lab_5.report;

import com.demkom58.db_lab_5.DatabaseManager;

import javax.swing.*;
import java.util.concurrent.ExecutorService;

public class JasperReporter {
    private final DatabaseManager databaseManager;
    private final ExecutorService service;

    private final JMenuItem simpleItem;
    private final JMenuItem nestedItem;
    private final JMenuItem statItem;

    public JasperReporter(DatabaseManager databaseManager, ExecutorService service,
                          JMenuItem simpleItem, JMenuItem nestedItem, JMenuItem statItem) {
        this.databaseManager = databaseManager;
        this.service = service;
        this.simpleItem = simpleItem;
        this.nestedItem = nestedItem;
        this.statItem = statItem;
    }
}
