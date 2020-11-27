package com.demkom58.db_lab_4.view;

import com.demkom58.db_lab_4.DatabaseManager;
import com.demkom58.db_lab_4.request.master.MasterController;
import com.demkom58.db_lab_4.request.parametred.ParametredController;
import com.demkom58.db_lab_4.request.paramless.ParamlessController;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainGui extends JFrame {
    private JPanel rootPanel;

    private JTabbedPane tabbedPane;
    private JPanel paramlessJPanel;
    private JPanel parametredJPanel;
    private JPanel masterPanel;

    private JTextField paramlessSqlField;
    private JButton paramlessGoButton;
    private JTable paramlessResultTable;
    private JLabel paramlessStatusBar;

    private final DatabaseManager databaseManager = new DatabaseManager();
    private final ExecutorService service = Executors.newFixedThreadPool(1, r -> {
        Thread t = Executors.defaultThreadFactory().newThread(r);
        t.setDaemon(true);
        return t;
    });

    private final ParamlessController paramlessController;
    private final ParametredController parametredController;
    private final MasterController masterController;

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
            JOptionPane.showMessageDialog(this, e.getLocalizedMessage(),
                    "Error occurred while setup", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(databaseManager::shutdown));

        this.paramlessController = new ParamlessController(tabbedPane, paramlessJPanel, databaseManager, service,
                paramlessSqlField, paramlessGoButton, paramlessResultTable, paramlessStatusBar);
        this.parametredController = new ParametredController(tabbedPane, parametredJPanel, databaseManager, service);
        this.masterController = new MasterController(tabbedPane, masterPanel, databaseManager, service);
    }

}
