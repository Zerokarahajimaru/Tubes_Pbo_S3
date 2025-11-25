package com.vending.model;

public class Product {
    private int id; // Tambahan ID untuk Database
    private String name;
    private int price;

    // Constructor untuk ambil data dari DB (ada ID)
    public Product(int id, String name, int price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    // Constructor untuk Insert baru (ID belum ada/auto-increment)
    public Product(String name, int price) {
        this.name = name;
        this.price = price;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getPrice() { return price; }
    
    @Override
    public String toString() {
        return String.format("%s - Rp%d", name, price);
    }
}