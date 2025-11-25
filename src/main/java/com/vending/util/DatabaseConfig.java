package com.vending.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseConfig {
    // Nama file database (akan muncul di folder proyek)
    private static final String URL = "jdbc:sqlite:vending_machine.db";

    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL);
        } catch (Exception e) {
            System.out.println("Koneksi DB Gagal: " + e.getMessage());
        }
        return conn;
    }

    // Method untuk membuat tabel otomatis saat aplikasi pertama kali jalan
    public static void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS products ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "name TEXT NOT NULL,"
                + "price INTEGER NOT NULL"
                + ");";

        try (Connection conn = connect();
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (Exception e) {
            System.out.println("Gagal buat tabel: " + e.getMessage());
        }
    }
}