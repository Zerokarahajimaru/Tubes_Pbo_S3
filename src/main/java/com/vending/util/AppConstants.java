package com.vending.util;

import java.util.Set;

public class AppConstants {
    // Valid input money
    public static final Set<Integer> ACCEPTED_BILLS = Set.of(5000, 10000, 20000, 50000);
    // Valid output change (sorted descending for greedy algo)
    public static final int[] CHANGE_DENOMINATIONS = {20000, 10000, 5000, 2000, 1000, 500};
}