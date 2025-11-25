package com.vending.core;

import com.vending.model.Product;
import com.vending.util.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    public List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products";

        try (Connection conn = DatabaseConfig.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Product(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getInt("price")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public void insertProduct(String name, int price) {
        String sql = "INSERT INTO products(name, price) VALUES(?,?)";
        try (Connection conn = DatabaseConfig.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, price);
            pstmt.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void updateProduct(int id, String name, int price) {
        String sql = "UPDATE products SET name = ?, price = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, price);
            pstmt.setInt(3, id);
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
        String sqlDelete = "DELETE FROM products";
        String sqlResetSeq = "DELETE FROM sqlite_sequence WHERE name='products'"; 
        
        try (Connection conn = DatabaseConfig.connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sqlDelete);
            stmt.execute(sqlResetSeq);
        } catch (Exception e) { e.printStackTrace(); }
    }
}