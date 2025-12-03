package com.vending.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseConfig {
    private static final String URL = "jdbc:postgresql://localhost:5432/vending_db";
    
    private static final String USER = "postgres"; 
    private static final String PASS = ""; // ISI PASSWORD PG ADMIN JANGAN KOSONG!!!!!!!!!!!!!!!!!!!!!!! //

    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL, USER, PASS);
        } catch (Exception e) {
            System.out.println("Koneksi PostgreSQL Gagal: " + e.getMessage());
        }
        return conn;
    }

    public static void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS products ("
                + "id SERIAL PRIMARY KEY," 
                + "name VARCHAR(100) NOT NULL,"
                + "price INT NOT NULL,"
                + "quantity INT DEFAULT 10"
                + ");";

        try (Connection conn = connect();
            Statement stmt = conn.createStatement()) {
            if (conn != null) {
                stmt.execute(sql);
            }
        } catch (Exception e) {
            System.out.println("Gagal buat tabel: " + e.getMessage());
        }
    }
}