package com.vending.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConfig {
    private static final Logger logger = Logger.getLogger(DatabaseConfig.class.getName());

    private static final String URL = "jdbc:postgresql://localhost:5432/vending_db";
    

    private static final String USER = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : "postgres";
    private static final String PASS = System.getenv("DB_PASS") != null ? System.getenv("DB_PASS") : ""; // ISI PASSWORD PG ADMIN JANGAN KOSONG!!!!!!!!!!!!!!!!!!!!!!! //

    // Menambah private constructor
    private DatabaseConfig() {
        throw new IllegalStateException("Utility class");
    }

    // Melempar SQLException daripada NULL
    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
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
            
            stmt.execute(sql);
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Gagal buat tabel: {0}", e.getMessage());
        }
    }
}