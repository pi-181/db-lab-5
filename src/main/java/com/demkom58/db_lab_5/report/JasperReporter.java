package com.demkom58.db_lab_5.report;

import com.demkom58.db_lab_5.DatabaseManager;
import com.demkom58.db_lab_5.util.ThrowableRunnable;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.swing.JRViewer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
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

        simpleItem.addActionListener(this::onSimpleReport);
        nestedItem.addActionListener(this::onNestedReport);
        statItem.addActionListener(this::onStatReport);
    }

    protected void onSimpleReport(ActionEvent e) {
        execute(() -> {
            try (Connection c = databaseManager.getConnection()) {
                showReport("Simple Report", "/jr/Simple.jrxml", new HashMap<>(), c);
            }
        });
    }

    protected void onNestedReport(ActionEvent e) {
        execute(() -> {
            try (Connection c = databaseManager.getConnection()) {
                showReport("Nested Report", "/jr/Nested.jrxml", new HashMap<String, Object>() {{
                    put("groups_subreport", load("/jr/NestedSub_Group.jrxml"));
                    put("subscribers_subreport", load("/jr/NestedSub_Subscribers.jrxml"));
                }}, c);
            }
        });
    }

    protected void onStatReport(ActionEvent e) {
        execute(() -> {
            try (Connection c = databaseManager.getConnection()) {
                showReport("Stats Report", "/jr/Stats.jrxml", new HashMap<>(), c);
            }
        });
    }

    private void showReport(String name, String template, Map<String, Object> parameters, Connection connection) throws IOException, JRException {
        JasperPrint jasperPrint = JasperFillManager.fillReport(load(template), parameters, connection);
        JFrame frame = new JFrame(name);
        frame.getContentPane().add(new JRViewer(jasperPrint));
        frame.setSize(850, 700);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void execute(ThrowableRunnable runnable) {
        service.execute(() -> {
            try {
                runnable.run();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                JOptionPane.showMessageDialog(null, throwable.getMessage(),
                        "Error occurred", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private JasperReport load(String report) throws JRException {
        return JasperCompileManager.compileReport(JRXmlLoader.load((getClass().getResourceAsStream(report))));
    }

}
