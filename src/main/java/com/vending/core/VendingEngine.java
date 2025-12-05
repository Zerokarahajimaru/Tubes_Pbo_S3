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
    private Inventory<Product> inventory; 

    private List<VendingObserver> observers = new ArrayList<>();

    // Private constructor untuk Singleton Pattern
    private VendingEngine() {
        DatabaseConfig.createTable();
        productDAO = new ProductDAO();
        
        // Inisialisasi wadah inventory generic
        inventory = new Inventory<>();

        // Jika database kosong, isi dengan data dummy
        if (productDAO.isEmpty()) {
            initializeDummyData();
        }
        
        // Sinkronisasi data awal dari database ke inventory (RAM)
        refreshInventoryState();
    }

    // Mendapatkan instance Singleton VendingEngine
    public static synchronized VendingEngine getInstance() {
        if (instance == null) instance = new VendingEngine();
        return instance;
    }

    // Mengambil data terbaru dari Database dan menyimpannya ke Inventory (Cache)
    private void refreshInventoryState() {
        this.inventory = new Inventory<>();
        List<Product> dbProducts = productDAO.getAllProducts();
        
        for (Product p : dbProducts) {
            this.inventory.addItem(p);
        }
    }

    // Memasukkan data awal produk ke database
    private void initializeDummyData() {
        productDAO.insertProduct("Coklat Bar", 12500, 25);
        productDAO.insertProduct("Keripik Kentang", 15000, 15);
        productDAO.insertProduct("Air Mineral", 5000, 30);
        productDAO.insertProduct("Teh Botol", 7500, 50);
        productDAO.insertProduct("Kopi Kaleng", 10000, 70);
    }

    // Mengambil daftar produk dari Inventory (In-Memory) untuk performa baca yang cepat
    public List<Product> getProducts() {
        return inventory.getItems();
    }

    // Menambah produk baru ke database dan melakukan refresh inventory
    public void addNewProduct(String name, int price, int quantity) {
        productDAO.insertProduct(name, price, quantity);
        refreshInventoryState();
        notifyUpdate("Admin: Menambah produk " + name, currentBalance);
    }

    // Mengupdate data produk di database dan melakukan refresh inventory
    public void updateProduct(int index, String name, int price, int quantity) {
        Product p = inventory.getItem(index); 
        productDAO.updateProduct(p.getId(), name, price, quantity);
        refreshInventoryState(); 
        notifyUpdate("Admin: Mengedit produk " + name, currentBalance);
    }

    // Menghapus produk dari database dan melakukan refresh inventory
    public void deleteProduct(int index) {
        Product p = inventory.getItem(index);
        productDAO.deleteProduct(p.getId());
        refreshInventoryState(); 
        notifyUpdate("Admin: Menghapus produk", currentBalance);
    }

    // Mendaftarkan observer (UI) untuk menerima update status
    public void addObserver(VendingObserver observer) { observers.add(observer); }
    
    // Mendapatkan saldo saat ini
    public int getBalance() { return currentBalance; }
    
    // Memasukkan uang ke mesin dengan validasi pecahan
    public void insertMoney(int amount) {
        if (AppConstants.ACCEPTED_BILLS.contains(amount)) {
            currentBalance += amount;
            notifyUpdate("Saldo masuk: Rp" + amount, currentBalance);
        } else {
            notifyError("Uang tidak diterima!");
        }
    }

    // Memproses pembelian barang: cek stok, cek saldo, update DB, dan update Inventory
    public boolean purchase(int index) {
        try {
            Product p = inventory.getItem(index);
            
            // Cek ketersediaan stok
            if (p.getQuantity() <= 0) {
                notifyError("Stok Habis! Silahkan pilih yang lain.");
                return false;
            }

            // Cek kecukupan saldo
            if (currentBalance >= p.getPrice()) {
                currentBalance -= p.getPrice();
                
                // Kurangi stok di database
                productDAO.decreaseStock(p.getId());
                
                // Update state inventory agar sinkron dengan database
                refreshInventoryState(); 
                
                Product updatedP = inventory.getItem(index);
                
                notifyUpdate("Berhasil beli " + updatedP.getName() + ". Sisa Saldo: Rp" + currentBalance, currentBalance);
                return true;
            } else {
                notifyError("Uang tidak cukup!");
                return false;
            }
        
        } catch (RuntimeException e) {
            notifyError("Error: " + e.getMessage());
            return false;
        }
    }

    // Menyelesaikan transaksi, menghitung kembalian, dan mereset saldo
    public String finishTransaction() {
        String change = calculateChange(currentBalance);
        currentBalance = 0;
        notifyUpdate("Transaksi Selesai", 0);
        return change;
    }
    
    // Algoritma greedy untuk menghitung pecahan uang kembalian
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
    
    // Validasi password admin sederhana
    public boolean checkAdminPassword(String p) { return "admin123".equals(p); }
    
    // Memberikan notifikasi update ke semua observer
    private void notifyUpdate(String msg, int balance) {
        for (VendingObserver obs : observers) obs.onStateChanged(msg, balance);
    }

    // Memberikan notifikasi error ke semua observer
    private void notifyError(String err) {
        for (VendingObserver obs : observers) obs.onErrorOccurred(err);
    }

    // Reset saldo manual
    public void reset() { currentBalance = 0; }

    // Reset total untuk keperluan unit testing (Data & Saldo)
    public void resetForUnitTesting() {
        this.currentBalance = 0;
        productDAO.clearAllData();
        initializeDummyData();
        refreshInventoryState(); 
    }
}