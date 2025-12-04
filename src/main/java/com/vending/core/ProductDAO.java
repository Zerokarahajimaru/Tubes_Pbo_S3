package com.vending.core;

import com.vending.model.Product;
import com.vending.util.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProductDAO {

    // Menambah Logger untuk menggantikan e.printStackTrace()
    private static final Logger logger = Logger.getLogger(ProductDAO.class.getName());

    public List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();
        
        // Jangan pake SELECT *, ga clean code
        String sql = "SELECT id, name, price, quantity FROM products ORDER BY id ASC"; 

        try (Connection conn = DatabaseConfig.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                list.add(new Product(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getInt("price"),
                    rs.getInt("quantity")
                ));
            }
        } catch (SQLException e) {
            // Menggunakan Logger dan menangkap SQLException spesifik
            logger.log(Level.SEVERE, "Gagal mengambil data produk", e);
        }
        return list;
    }

    public void insertProduct(String name, int price, int quantity) {
        String sql = "INSERT INTO products(name, price, quantity) VALUES(?,?,?)";
        try (Connection conn = DatabaseConfig.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, price);
            pstmt.setInt(3, quantity);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Gagal insert produk", e);
        }
    }

    public void updateProduct(int id, String name, int price, int quantity) {
        String sql = "UPDATE products SET name = ?, price = ?, quantity = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, price);
            pstmt.setInt(3, quantity);
            pstmt.setInt(4, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Gagal update produk", e);
        }
    }

    public void decreaseStock(int id) {
        String sql = "UPDATE products SET quantity = quantity - 1 WHERE id = ?";
        try (Connection conn = DatabaseConfig.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Gagal kurangi stok", e);
        }
    }

    public void deleteProduct(int id) {
        String sql = "DELETE FROM products WHERE id = ?";
        try (Connection conn = DatabaseConfig.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Gagal hapus produk", e);
        }
    }
    
    public boolean isEmpty() {
        String sql = "SELECT count(*) FROM products";
        try (Connection conn = DatabaseConfig.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Gagal cek tabel kosong", e);
        }
        return true;
    }
    
    public void clearAllData() {
        String sql = "TRUNCATE TABLE products RESTART IDENTITY"; 
        
        try (Connection conn = DatabaseConfig.connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            
        } catch (SQLException e) { 
            logger.log(Level.SEVERE, "Gagal reset data", e); 
        }
    }
}