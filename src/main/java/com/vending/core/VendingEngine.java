package com.vending.core;

import com.vending.model.Product;
import com.vending.patterns.VendingObserver;
import com.vending.util.AppConstants;
import com.vending.util.DatabaseConfig;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("java:S6548")
public class VendingEngine {
    
    private static VendingEngine instance; 
    private int currentBalance = 0;
    
    private ProductDAO productDAO; 
    private List<VendingObserver> observers = new ArrayList<>();

    private VendingEngine() {
        DatabaseConfig.createTable();
        productDAO = new ProductDAO();
        if (productDAO.isEmpty()) {
            initializeDummyData();
        }
    }

    public static synchronized VendingEngine getInstance() {
        if (instance == null) instance = new VendingEngine();
        return instance;
    }

    private void initializeDummyData() {
        productDAO.insertProduct("Coklat Bar", 12500, 25);
        productDAO.insertProduct("Keripik Kentang", 15000, 15);
        productDAO.insertProduct("Air Mineral", 5000, 30);
        productDAO.insertProduct("Teh Botol", 7500, 50);
        productDAO.insertProduct("Kopi Kaleng", 10000, 70);
    }

    public List<Product> getProducts() {
        return productDAO.getAllProducts();
    }

    public void addNewProduct(String name, int price, int quantity) {
        productDAO.insertProduct(name, price, quantity);
        notifyUpdate("Admin: Menambah produk " + name, currentBalance);
    }

    public void updateProduct(int index, String name, int price, int quantity) {
        Product p = getProducts().get(index);
        productDAO.updateProduct(p.getId(), name, price, quantity);
        notifyUpdate("Admin: Mengedit produk " + name, currentBalance);
    }

    public void deleteProduct(int index) {
        Product p = getProducts().get(index);
        productDAO.deleteProduct(p.getId());
        notifyUpdate("Admin: Menghapus produk", currentBalance);
    }

    public void addObserver(VendingObserver observer) { observers.add(observer); }
    public int getBalance() { return currentBalance; }
    
    public void insertMoney(int amount) {
        if (AppConstants.ACCEPTED_BILLS.contains(amount)) {
            currentBalance += amount;
            notifyUpdate("Saldo masuk: Rp" + amount, currentBalance);
        } else {
            notifyError("Uang tidak diterima!");
        }
    }

    // --- BAGIAN YANG DIPERBAIKI (HANYA INI) ---
    public boolean purchase(int index) {
        try {
            List<Product> products = getProducts();
            
            // GANTI 1: Exception -> IllegalArgumentException (Sesuai saran SonarQube)
            if (index < 0 || index >= products.size()) throw new IllegalArgumentException("Produk tidak valid");
            
            Product p = products.get(index);
            
            // 1. Cek Stok Habis
            if (p.getQuantity() <= 0) {
                notifyError("Stok Habis! Silahkan pilih yang lain.");
                return false;
            }

            // 2. Cek Uang
            if (currentBalance >= p.getPrice()) {
                currentBalance -= p.getPrice();
                
                // 3. Kurangi Stok di Database
                productDAO.decreaseStock(p.getId());
                
                notifyUpdate("Berhasil beli " + p.getName() + ". Sisa Saldo: Rp" + currentBalance, currentBalance);
                return true;
            } else {
                notifyError("Uang tidak cukup!");
                return false;
            }
        // GANTI 2: Catch Exception -> RuntimeException (Biar match sama IllegalArgumentException)
        } catch (RuntimeException e) {
            notifyError("Error: " + e.getMessage());
            return false;
        }
    }
    // --- AKHIR BAGIAN YANG DIPERBAIKI ---

    public String finishTransaction() {
        String change = calculateChange(currentBalance);
        currentBalance = 0;
        notifyUpdate("Transaksi Selesai", 0);
        return change;
    }
    
    private String calculateChange(int amount) {
        if (amount == 0) return "Uang pas.";
        StringBuilder sb = new StringBuilder("Kembalian:\n");
        for (int denom : AppConstants.CHANGE_DENOMINATIONS) {
            int count = 0;
            while (amount >= denom) {
                amount -= denom;
                count++;
            }
            if (count > 0) sb.append("Rp").append(denom).append(" x ").append(count).append("\n");
        }
        return sb.toString();
    }
    
    public boolean checkAdminPassword(String p) { return "admin123".equals(p); }
    
    private void notifyUpdate(String msg, int balance) {
        for (VendingObserver obs : observers) obs.onStateChanged(msg, balance);
    }
    private void notifyError(String err) {
        for (VendingObserver obs : observers) obs.onErrorOccurred(err);
    }
    public void reset() { currentBalance = 0; }

    public void resetForUnitTesting() {
        this.currentBalance = 0;
        productDAO.clearAllData();
        initializeDummyData();
    }
}