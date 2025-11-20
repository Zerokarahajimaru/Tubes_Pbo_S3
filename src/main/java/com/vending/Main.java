package com.vending;

import com.vending.ui.VendingUI;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Clean Code: Menjalankan GUI di Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            new VendingUI().setVisible(true);
        });
    }
}