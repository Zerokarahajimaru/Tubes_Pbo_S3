package com.vending.patterns;

public interface VendingObserver {
    void onStateChanged(String message, int currentBalance);
    void onErrorOccurred(String errorMessage);
}