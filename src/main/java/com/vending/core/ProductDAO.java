package com.vending.core;

import com.vending.model.Product;
import com.vending.util.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    // 1. Update SELECT
    public List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products ORDER BY id ASC"; // Tambah ORDER BY biar rapi
        try (Connection conn = DatabaseConfig.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Product(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getInt("price"),
                    rs.getInt("quantity") // <--- Ambil Stok
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // 2. Update INSERT (Admin nambah barang + stok)
    public void insertProduct(String name, int price, int quantity) {
        String sql = "INSERT INTO products(name, price, quantity) VALUES(?,?,?)";
        try (Connection conn = DatabaseConfig.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, price);
            pstmt.setInt(3, quantity);
            pstmt.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    // 3. Update UPDATE (Admin edit stok)
    public void updateProduct(int id, String name, int price, int quantity) {
        String sql = "UPDATE products SET name = ?, price = ?, quantity = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, price);
            pstmt.setInt(3, quantity);
            pstmt.setInt(4, id);
            pstmt.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    // 4. METHOD BARU: Kurangi Stok saat beli
    public void decreaseStock(int id) {
        String sql = "UPDATE products SET quantity = quantity - 1 WHERE id = ?";
        try (Connection conn = DatabaseConfig.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void deleteProduct(int id) {
        String sql = "DELETE FROM products WHERE id = ?";
        try (Connection conn = DatabaseConfig.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }
    
    public boolean isEmpty() {
        String sql = "SELECT count(*) FROM products";
        try (Connection conn = DatabaseConfig.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        } catch (Exception e) { e.printStackTrace(); }
        return true;
    }
    
    public void clearAllData() {
        // Syntax PostgreSQL untuk hapus data & reset sequence ID ke 1
        String sql = "TRUNCATE TABLE products RESTART IDENTITY"; 
        
        try (Connection conn = DatabaseConfig.connect();
             Statement stmt = conn.createStatement()) {
            if (conn != null) {
                stmt.execute(sql);
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
    }
}