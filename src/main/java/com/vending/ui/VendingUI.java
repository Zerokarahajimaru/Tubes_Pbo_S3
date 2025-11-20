package com.vending.ui;

import com.vending.model.Product;
import com.vending.patterns.VendingFacade;
import com.vending.patterns.VendingObserver;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class VendingUI extends JFrame implements VendingObserver {
    private VendingFacade facade;
    private JLabel balanceLabel;
    private JTextArea infoArea;

    public VendingUI() {
        this.facade = new VendingFacade();
        this.facade.registerObserver(this); // Hubungkan Observer
        initUI();
    }

    private void initUI() {
        setTitle("Java Vending Machine");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- Panel Produk (Center) ---
        JPanel productPanel = new JPanel(new GridLayout(2, 5, 10, 10));
        productPanel.setBorder(BorderFactory.createTitledBorder("List Makanan"));
        
        List<Product> products = facade.getProductList();
        for (int i = 0; i < products.size(); i++) {
            Product p = products.get(i);
            JButton btn = new JButton("<html><center>" + p.getName() + "<br>Rp" + p.getPrice() + "</center></html>");
            int index = i; // variable for lambda
            btn.addActionListener(e -> facade.buyProduct(index));
            productPanel.add(btn);
        }

        // --- Panel Uang (Right) ---
        JPanel moneyPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        moneyPanel.setBorder(BorderFactory.createTitledBorder("Masukan Uang"));
        
        int[] validMoney = {5000, 10000, 20000, 50000};
        for (int m : validMoney) {
            JButton btn = new JButton("Rp " + m);
            btn.addActionListener(e -> facade.insertMoney(m));
            moneyPanel.add(btn);
        }

        // --- Panel Info & Balance (Bottom) ---
        JPanel bottomPanel = new JPanel(new BorderLayout());
        balanceLabel = new JLabel("Saldo: Rp 0");
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 20));
        balanceLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        infoArea = new JTextArea(5, 20);
        infoArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(infoArea);

        bottomPanel.add(balanceLabel, BorderLayout.NORTH);
        bottomPanel.add(scroll, BorderLayout.CENTER);

        add(productPanel, BorderLayout.CENTER);
        add(moneyPanel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
        
        setLocationRelativeTo(null);
    }

    // --- Observer Methods ---
    @Override
    public void onStateChanged(String message, int currentBalance) {
        balanceLabel.setText("Saldo: Rp " + currentBalance);
        infoArea.append("INFO: " + message + "\n");
        infoArea.setCaretPosition(infoArea.getDocument().getLength());
    }

    @Override
    public void onErrorOccurred(String errorMessage) {
        JOptionPane.showMessageDialog(this, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
        infoArea.append("ERROR: " + errorMessage + "\n");
    }
}