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

    public void insertMoney(int amount) {
        engine.insertMoney(amount);
    }

    public void buyProduct(int index) {
        engine.purchase(index);
    }
}