package com.vending.util;

import java.util.List;
import java.util.Set;

// 1. Tambahkan 'final' di class agar tidak bisa di-inherit
public final class AppConstants {

    // Valid input money
    public static final Set<Integer> ACCEPTED_BILLS = Set.of(5000, 10000, 20000, 50000);

    // 2. Ganti int[] menjadi List<Integer> agar benar-benar immutable (tidak bisa diedit isinya)
    // List.of() otomatis membuat list yang unmodifiable.
    public static final List<Integer> CHANGE_DENOMINATIONS = List.of(20000, 10000, 5000, 2000, 1000, 500);

    // 3. Tambahkan private constructor untuk menyembunyikan public constructor default
    private AppConstants() {
        // Mencegah instansiasi class ini
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}