package com.vending;

import com.vending.core.VendingEngine;
import com.vending.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VendingTest {
    
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
        
        assertNotEquals(-1, index, "Barang harga 12500 harus ada di dummy data");

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
        //alternatif kalo g ada barang yang harga 12500
        if (index == -1) index = findProductIndexByPrice(10000);

        boolean isSuccess = engine.purchase(index);
        
        assertFalse(isSuccess);
        assertEquals(5000, engine.getBalance());
    }

    @Test
    void testGreedyChangeCalculation() {
        engine.insertMoney(20000); 
        
        int targetIndex = findProductIndexByPrice(5000);
        // 2. Perbaikan yang sama: assertNotEquals
        assertNotEquals(-1, targetIndex, "Harus ada produk seharga 5000");
        
        engine.purchase(targetIndex); 
        
        String changeMsg = engine.finishTransaction();
        
        // 3. Clean Code: Gunakan assertAll biar kalau error ketahuan bagian mana yang hilang
        assertAll("Cek Detail Kembalian",
            () -> assertTrue(changeMsg.contains("Rp10000"), "Kembalian harus mengandung Rp10000"),
            () -> assertTrue(changeMsg.contains("Rp5000"), "Kembalian harus mengandung Rp5000")
        );
    }

    @Test
    void testBuyOutOfStock() {
        engine.insertMoney(50000);
        int index = 0;
        Product p = engine.getProducts().get(index);
        engine.updateProduct(index, p.getName(), p.getPrice(), 0);
        boolean isSuccess = engine.purchase(index);
        assertFalse(isSuccess);
        assertEquals(50000, engine.getBalance(), "Saldo tidak boleh terpotong");
    }

    @Test
    void testaddNewProduct() {
        String namaproduk = "Susu Kedelai";
        int hargaproduk = 15000;
        int jumlahproduk = 20;

        engine.addNewProduct(namaproduk, hargaproduk, jumlahproduk);
        Product found = null;
        for (Product p : engine.getProducts()) {
            if (p.getName().equals(namaproduk)){
                found = p;
                break;
            }
        }

        assertNotNull(found, "Produk baru harus ditemukan di database");
        assertEquals(hargaproduk, found.getPrice());
        assertEquals(jumlahproduk, found.getQuantity());
    }

    @Test
    void testDeleteProduct() {
        int initialSize = engine.getProducts().size();
        engine.deleteProduct(0);
        int newSize = engine.getProducts().size();
        assertEquals(initialSize - 1, newSize, "Ukuran produk harus berkurang setelah penghapusan");
    }

    @Test
    void testUpdatePrice() {
        int index = 0;
        Product p = engine.getProducts().get(index);
        engine.updateProduct(index, p.getName(), 20000, p.getQuantity());
        Product updated = engine.getProducts().get(index);
        assertEquals(20000, updated.getPrice(), "Harga produk harus terupdate");
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