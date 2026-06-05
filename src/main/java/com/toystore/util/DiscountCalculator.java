package com.toystore.util;

public abstract class DiscountCalculator {
    protected static final double REGULAR_DISCOUNT = 0.05; // 5% discount
    protected static final double VIP_DISCOUNT = 0.15; // 15% discount
    
    public abstract double calculateDiscount(double amount);
    
    public static class RegularUserDiscount extends DiscountCalculator {
        @Override
        public double calculateDiscount(double amount) {
            return amount * REGULAR_DISCOUNT;
        }
    }
    
    public static class VIPUserDiscount extends DiscountCalculator {
        @Override
        public double calculateDiscount(double amount) {
            return amount * VIP_DISCOUNT;
        }
    }
    
    public static DiscountCalculator getDiscountCalculator(String userType) {
        if ("VIP".equalsIgnoreCase(userType)) {
            return new VIPUserDiscount();
        }
        return new RegularUserDiscount();
    }
} 