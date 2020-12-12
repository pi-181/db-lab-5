package com.demkom58.db_lab_5;

import com.demkom58.db_lab_5.view.MainGui;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        EventQueue.invokeLater(() -> new MainGui().setVisible(true));
    }
}
