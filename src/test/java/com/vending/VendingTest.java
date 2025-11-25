package com.vending;

import com.vending.core.VendingEngine;
import com.vending.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class VendingTest {
    
    private VendingEngine engine;

    @BeforeEach
    void setUp() {
        engine = VendingEngine.getInstance();
        engine.resetForUnitTesting(); 
    }

    @Test
    void testInsertValidMoney() {
        engine.insertMoney(20000);
        assertEquals(20000, engine.getBalance());
    }

    @Test
    void testInsertInvalidMoney() {
        engine.insertMoney(3000);
        assertEquals(0, engine.getBalance());
    }

    @Test
    void testPurchaseSuccessFlow() {
        engine.insertMoney(20000);
        
        int index = findProductIndexByPrice(12500);
        assertTrue(index != -1, "Barang harga 12500 harus ada di dummy data");

        boolean isSuccess = engine.purchase(index); 
        assertTrue(isSuccess);

        assertEquals(7500, engine.getBalance());

        engine.finishTransaction();
        assertEquals(0, engine.getBalance());
    }
    
    @Test
    void testPurchaseInsufficientFunds() {
        engine.insertMoney(5000);
        
        int index = findProductIndexByPrice(12500);
        // Fallback jika tidak ada barang 12500
        if (index == -1) index = findProductIndexByPrice(10000);

        boolean isSuccess = engine.purchase(index);
        
        assertFalse(isSuccess);
        assertEquals(5000, engine.getBalance());
    }

    @Test
    void testGreedyChangeCalculation() {
        engine.insertMoney(20000); 
        
        int targetIndex = findProductIndexByPrice(5000);
        assertTrue(targetIndex != -1, "Harus ada produk seharga 5000");
        
        engine.purchase(targetIndex); 
        
        String changeMsg = engine.finishTransaction();
        
        assertTrue(changeMsg.contains("Rp10000") && changeMsg.contains("Rp5000"), 
                   "Kembalian Salah! Output: " + changeMsg);
    }
    
    // Helper
    private int findProductIndexByPrice(int price) {
        List<Product> products = engine.getProducts();
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getPrice() == price) {
                return i;
            }
        }
        return -1;
    }
}