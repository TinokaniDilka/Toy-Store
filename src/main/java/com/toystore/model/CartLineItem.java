package com.toystore.model;

public class CartLineItem {
    private String toyId;
    private String toyName;
    private double price;
    private int quantity;

    public CartLineItem(String toyId, String toyName, double price, int quantity) {
        this.toyId = toyId;
        this.toyName = toyName;
        this.price = price;
        this.quantity = quantity;
    }

    public String getToyId() {
        return toyId;
    }

    public String getToyName() {
        return toyName;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getSubtotal() {
        return price * quantity;
    }
}
