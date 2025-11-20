package com.vending;

import com.vending.core.VendingEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class VendingTest {
    
    private VendingEngine engine;

    @BeforeEach
    void setUp() {
        engine = VendingEngine.getInstance();
        engine.reset(); // Reset state singleton sebelum tiap test
    }

    @Test
    void testInsertValidMoney() {
        engine.insertMoney(20000);
        assertEquals(20000, engine.getBalance());
    }

    @Test
    void testInsertInvalidMoney() {
        engine.insertMoney(3000); // Tidak ada di list valid
        assertEquals(0, engine.getBalance());
    }

    @Test
    void testPurchaseSuccess() {
        engine.insertMoney(20000);
        // Beli item index 0 (Harga 12500)
        engine.purchase(0); 
        // Setelah beli sukses, saldo direset di logic engine karena kembalian dikeluarkan
        assertEquals(0, engine.getBalance());
    }
    
    @Test
    void testPurchaseInsufficientFunds() {
        engine.insertMoney(5000);
        // Beli item index 0 (Harga 12500)
        engine.purchase(0);
        // Saldo harus tetap 5000 karena gagal beli
        assertEquals(5000, engine.getBalance());
    }
}