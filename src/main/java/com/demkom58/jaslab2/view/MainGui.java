package com.demkom58.jaslab2.view;

import javax.swing.*;
import java.awt.Dimension;

public class MainGui extends JFrame {
    private JPanel rootPanel;
    private JTextField sqlField;
    private JButton goButton;
    private JTable resultTable;
    private JLabel statusBar;

    public MainGui() {
        setContentPane(rootPanel);

        final Dimension minSize = new Dimension(450, 400);
        setMaximumSize(minSize);
        setSize(minSize);

        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setTitle("Laboratory Work №2");
    }

}
