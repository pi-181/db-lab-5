package com.demkom58.jaslab2.view;

import javax.swing.*;
import java.awt.*;

public class MainGui extends JFrame {
    private final Dimension minSize = new Dimension(450, 400);
    private JPanel rootPanel;

    public MainGui() {
        setContentPane(rootPanel);
        setMaximumSize(minSize);
        setSize(minSize);
        setLocationRelativeTo(null);
        setTitle("Laboratory Work №2");
    }

}
