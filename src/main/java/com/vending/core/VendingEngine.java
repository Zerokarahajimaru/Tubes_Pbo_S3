package com.vending.core;

import com.vending.model.Product;
import com.vending.patterns.VendingObserver;
import com.vending.util.AppConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VendingEngine {
    // Singleton Instance
    private static VendingEngine instance;
    
    private int currentBalance = 0;
    private Inventory<Product> inventory;
    private List<VendingObserver> observers = new ArrayList<>();

    private VendingEngine() {
        inventory = new Inventory<>();
        initializeDummyData();
    }

    public static synchronized VendingEngine getInstance() {
        if (instance == null) {
            instance = new VendingEngine();
        }
        return instance;
    }

    private void initializeDummyData() {
        // 10 Dummy Data
        inventory.addItem(new Product("Coklat Bar", 12500));
        inventory.addItem(new Product("Keripik Kentang", 15000));
        inventory.addItem(new Product("Air Mineral", 5000));
        inventory.addItem(new Product("Teh Botol", 7500));
        inventory.addItem(new Product("Kopi Kaleng", 10000));
        inventory.addItem(new Product("Biskuit", 8000));
        inventory.addItem(new Product("Permen Mint", 4500));
        inventory.addItem(new Product("Roti Sobek", 11000));
        inventory.addItem(new Product("Susu Kotak", 6500));
        inventory.addItem(new Product("Mie Instan Cup", 9000));
    }

    public void addObserver(VendingObserver observer) {
        observers.add(observer);
    }

    public List<Product> getProducts() {
        return inventory.getItems();
    }

    public void insertMoney(int amount) {
        // Validasi Input sesuai Requirement
        if (AppConstants.ACCEPTED_BILLS.contains(amount)) {
            currentBalance += amount;
            notifyUpdate("Uang masuk: Rp" + amount, currentBalance);
        } else {
            notifyError("Uang pecahan Rp" + amount + " tidak diterima!");
        }
    }

    public void purchase(int index) {
        try {
            Product p = inventory.getItem(index);
            if (currentBalance >= p.getPrice()) {
                int cost = p.getPrice();
                currentBalance -= cost;
                
                // Kalkulasi Kembalian
                String changeDetails = calculateChange(currentBalance);
                
                // Reset Balance setelah transaksi selesai
                currentBalance = 0; 
                notifyUpdate("Berhasil membeli " + p.getName() + ". " + changeDetails, currentBalance);
            } else {
                notifyError("Uang tidak cukup! Kurang: Rp" + (p.getPrice() - currentBalance));
            }
        } catch (Exception e) {
            notifyError("Error: " + e.getMessage());
        }
    }

    // Algoritma Greedy untuk kembalian
    private String calculateChange(int amount) {
        if (amount == 0) return "Tidak ada kembalian.";
        
        StringBuilder sb = new StringBuilder("Kembalian: ");
        Map<Integer, Integer> changeMap = new HashMap<>();

        for (int denom : AppConstants.CHANGE_DENOMINATIONS) {
            while (amount >= denom) {
                amount -= denom;
                changeMap.put(denom, changeMap.getOrDefault(denom, 0) + 1);
            }
        }

        changeMap.forEach((k, v) -> sb.append("Rp").append(k).append("x").append(v).append(", "));
        return sb.toString();
    }

    private void notifyUpdate(String msg, int balance) {
        for (VendingObserver obs : observers) obs.onStateChanged(msg, balance);
    }

    private void notifyError(String err) {
        for (VendingObserver obs : observers) obs.onErrorOccurred(err);
    }
    
    // Helper for testing
    public int getBalance() { return currentBalance; }
    public void reset() { currentBalance = 0; }
}