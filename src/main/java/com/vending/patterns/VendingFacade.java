package com.vending.patterns;

import com.vending.core.VendingEngine;
import com.vending.model.Product;
import java.util.List;

public class VendingFacade {
    private VendingEngine engine;

    public VendingFacade() {
        this.engine = VendingEngine.getInstance();
    }

    public void registerObserver(VendingObserver observer) {
        engine.addObserver(observer);
    }

    public List<Product> getProductList() {
        return engine.getProducts();
    }

    public int getCurrentBalance() {
        return engine.getBalance();
    }

    public void insertMoney(int amount) {
        engine.insertMoney(amount);
    }

    public boolean buyProduct(int index) {
        return engine.purchase(index);
    }
    
    public String finishAndGetChange() {
        return engine.finishTransaction();
    }

    // Fitur admin //
    public boolean loginAdmin(String password) {
        return engine.checkAdminPassword(password);
    }

    public void addProduct(String name, int price) {
        engine.addNewProduct(name, price);
    }

    public void editProduct(int index, String name, int price) {
        engine.updateProduct(index, name, price);
    }

    public void removeProduct(int index) {
        engine.deleteProduct(index);
    }
}