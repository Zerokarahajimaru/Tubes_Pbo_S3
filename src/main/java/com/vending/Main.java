package com.vending;

import com.vending.ui.VendingUI;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VendingUI().setVisible(true));
    }
}