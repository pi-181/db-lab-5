package com.demkom58.jaslab2;

import com.demkom58.jaslab2.view.MainGui;

import java.awt.*;

public class Main {
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> new MainGui().setVisible(true));
    }
}
